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

public class VotesManager {
   private HashMap entries = new HashMap();

   public VotesManager(Skywars var1) {
      this.entries.put("Time", new VotesManager.Entry(var1, "Time", new ItemStack(Material.WATCH), Arrays.asList(ChatColor.YELLOW + "Sunrise", ChatColor.GOLD + "Noon", ChatColor.LIGHT_PURPLE + "Sunset", ChatColor.DARK_AQUA + "Midnight"), false, (VotesManager.Entry)null));
      ArrayList var2 = new ArrayList();
      Iterator var4 = var1.chests.keySet().iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         var2.add(ChatColor.YELLOW + var3);
      }

      this.entries.put("Chests", new VotesManager.Entry(var1, "Chests", new ItemStack(Material.CHEST), var2, false, (VotesManager.Entry)null));
      this.entries.put("Health", new VotesManager.Entry(var1, "Health", new ItemStack(Material.APPLE), Arrays.asList(ChatColor.RED + "10 Hearts", ChatColor.YELLOW + "20 Hearts", ChatColor.GOLD + "30 Hearts"), false, (VotesManager.Entry)null));
   }

   public void vote(Player var1, String var2, String var3) {
      ((VotesManager.Entry)this.entries.get(var2)).voters.put(var1.getName(), var3);
   }

   public void removeVotes(Player var1) {
      Iterator var3 = this.entries.values().iterator();

      while(var3.hasNext()) {
         VotesManager.Entry var2 = (VotesManager.Entry)var3.next();
         if (var2.voters.containsKey(var1.getName())) {
            var2.voters.remove(var1.getName());
         }
      }

   }

   public void openEntry(Player var1, String var2) {
      var1.openInventory(((VotesManager.Entry)this.entries.get(var2)).inventory);
   }

   public int getVotes(String var1, String var2) {
      int var3 = 0;
      HashMap var4 = ((VotesManager.Entry)this.entries.get(var1)).voters;
      Iterator var6 = var4.keySet().iterator();

      while(var6.hasNext()) {
         String var5 = (String)var6.next();
         if (((String)var4.get(var5)).equals(var2)) {
            ++var3;
         }
      }

      return var3;
   }

   public String getVoted(String var1) {
      VotesManager.Entry var2 = (VotesManager.Entry)this.entries.get(var1);
      String var3 = "Default";
      int var4 = 0;
      Iterator var6 = var2.options.iterator();

      while(var6.hasNext()) {
         String var5 = (String)var6.next();
         int var7 = this.getVotes(var1, var5);
         if (var7 > var4) {
            var3 = var5;
            var4 = var7;
         }
      }

      return var3;
   }

   public void reset() {
      Iterator var2 = this.entries.values().iterator();

      while(var2.hasNext()) {
         VotesManager.Entry var1 = (VotesManager.Entry)var2.next();
         var1.voters.clear();
      }

   }

   private class Entry {
      Inventory inventory;
      HashMap voters;
      List options;

      private Entry(Skywars var2, String var3, ItemStack var4, List var5, boolean var6) {
         this.voters = new HashMap();
         this.options = new ArrayList();
         Iterator var8 = var5.iterator();

         while(var8.hasNext()) {
            String var7 = (String)var8.next();
            this.options.add(ChatColor.stripColor(var7));
         }

         this.inventory = Bukkit.createInventory((InventoryHolder)null, var2.getInventorySize(var5.size() + 9), (String)var2.customization.inventories.get("Arena-Settings") + ": " + ChatColor.DARK_AQUA + var3);
         var2.cageInventory(this.inventory, true);

         for(int var9 = 0; var9 < var5.size(); ++var9) {
            this.inventory.setItem(var9, (new ItemStackBuilder(var4)).setName(var6 ? ChatColor.AQUA + (String)var5.get(var9) : (String)var5.get(var9)).build());
         }

         this.inventory.setItem(this.inventory.getSize() - 5, var2.back_itemstack);
      }
   }
}
