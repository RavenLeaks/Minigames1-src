package me.wazup.eggwars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class VillagerShop {
   private HashMap shops = new HashMap();

   public VillagerShop(Eggwars var1, FileConfiguration var2) {
      Iterator var4 = var2.getConfigurationSection("Shops").getKeys(false).iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         this.shops.put(var3, new VillagerShop.Shop(var1, var2, var3, (VillagerShop.Shop)null));
      }

   }

   public boolean containsShop(String var1) {
      return this.shops.containsKey(var1);
   }

   public void openShop(Player var1, String var2) {
      var1.openInventory(((VillagerShop.Shop)this.shops.get(var2)).inventory);
   }

   public void openCategory(Player var1, String var2, Integer var3) {
      var1.openInventory((Inventory)((VillagerShop.Shop)this.shops.get(var2)).categories.get(var3));
   }

   public int getShopsCount() {
      return this.shops.size();
   }

   private class Shop {
      private Inventory inventory;
      private HashMap categories;

      private Shop(Eggwars var2, FileConfiguration var3, String var4) {
         this.inventory = Bukkit.createInventory((InventoryHolder)null, var2.getInventorySize(var3.getConfigurationSection("Shops." + var4).getKeys(false).size()), (String)var2.customization.inventories.get("Villager-Shop"));
         this.categories = new HashMap();
         int var5 = 0;

         for(Iterator var7 = var3.getConfigurationSection("Shops." + var4).getKeys(false).iterator(); var7.hasNext(); ++var5) {
            String var6 = (String)var7.next();
            String var8 = "Shops." + var4 + "." + var6 + ".";
            ItemStackBuilder var9 = (new ItemStackBuilder(Material.getMaterial(var3.getString(var8 + "display_item")))).setName(ChatColor.translateAlternateColorCodes('&', var3.getString(var8 + "name")));
            Iterator var11 = var3.getStringList(var8 + "lore").iterator();

            while(var11.hasNext()) {
               String var10 = (String)var11.next();
               var9.addLore(ChatColor.translateAlternateColorCodes('&', var10));
            }

            ItemStack var20 = var9.build();
            this.inventory.setItem(var5, var20);
            List var21 = var3.getStringList(var8 + "items");
            Inventory var12 = Bukkit.createInventory((InventoryHolder)null, var2.getInventorySize(var21.size()) + 9, (String)var2.customization.inventories.get("Villager-Shop") + ": " + var20.getItemMeta().getDisplayName());
            var2.cageInventory(var12, true);
            int var13 = 0;

            for(Iterator var15 = var21.iterator(); var15.hasNext(); ++var13) {
               String var14 = (String)var15.next();
               String var16 = var14.split(" :: ")[0];
               String var17 = var14.split(" :: ")[1];
               ItemStack var18 = var2.getItemStack(var16, true, false);
               ItemStack var19 = (new ItemStackBuilder(var2.getItemStack(var17, true, true))).addLore(" ", ChatColor.GRAY + " - " + ChatColor.YELLOW + "Costs: " + ChatColor.AQUA + var18.getAmount() + " " + var18.getType().name() + "(S)").build();
               var12.setItem(var13, var19);
            }

            var12.setItem(var12.getSize() - 5, var2.back_itemstack);
            this.categories.put(var5, var12);
         }

      }

      // $FF: synthetic method
      Shop(Eggwars var2, FileConfiguration var3, String var4, VillagerShop.Shop var5) {
         this(var2, var3, var4);
      }
   }
}
