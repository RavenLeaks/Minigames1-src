package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SmartInventory {
   private String mainName;
   private HashMap inventories = new HashMap();
   private Kitbattle plugin;

   public SmartInventory(Kitbattle var1, String var2) {
      this.plugin = var1;
      this.mainName = var2 + ": ";
   }

   public int addInventory(String var1) {
      int var2 = this.inventories.size();
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 54, this.mainName + var1);
      this.plugin.cageInventory(var3, false);
      if (var2 != 0) {
         var3.setItem(18, this.plugin.previous_itemstack);
         var3.setItem(27, this.plugin.previous_itemstack);
         Inventory var4 = (Inventory)this.inventories.get(var2 - 1);
         var4.setItem(26, this.plugin.next_itemstack);
         var4.setItem(35, this.plugin.next_itemstack);
      }

      this.inventories.put(var2, var3);
      return var2;
   }

   public void setItem(int var1, int var2, ItemStack var3) {
      ((Inventory)this.inventories.get(var1)).setItem(var2, var3);
   }

   public void removeItem(int var1, int var2) {
      this.setItem(var1, var2, new ItemStack(Material.AIR));
      this.organize(var1);
   }

   public ItemStack getItem(int var1, int var2) {
      return ((Inventory)this.inventories.get(var1)).getItem(var2);
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

   public Entry getEmptySlot() {
      int var1 = -1;
      int var2 = 0;

      for(int var3 = 0; var3 < this.inventories.size(); ++var3) {
         if ((var1 = this.getEmptySlot(var3)) > -1) {
            var2 = var3;
            break;
         }
      }

      if (var1 == -1) {
         var2 = this.addInventory(ChatColor.RED + "List #" + (this.inventories.size() + 1));
         var1 = this.plugin.smartSlots[0];
      }

      return new SimpleEntry(var2, var1);
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

   public boolean contains(ItemStack var1) {
      for(int var2 = 0; var2 < this.inventories.size(); ++var2) {
         if (this.getContents(var2).contains(var1)) {
            return true;
         }
      }

      return false;
   }

   public List getAllContents() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.inventories.size(); ++var2) {
         var1.addAll(this.getContents(var2));
      }

      return var1;
   }

   public boolean handleClick(Player var1, ItemStack var2, Inventory var3) {
      if (!this.plugin.compareItem(var2, this.plugin.next_itemstack) && !this.plugin.compareItem(var2, this.plugin.previous_itemstack)) {
         return false;
      } else {
         int var4 = 0;

         for(int var5 = 0; var5 < this.inventories.size(); ++var5) {
            var4 = var5;
            if (((Inventory)this.inventories.get(var5)).equals(var3)) {
               break;
            }
         }

         if (this.plugin.compareItem(var2, this.plugin.next_itemstack)) {
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
