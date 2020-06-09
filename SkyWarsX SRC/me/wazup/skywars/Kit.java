package me.wazup.skywars;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class Kit extends Sellable {
   ArrayList items;
   ItemStack[] armor;

   public Kit(Skywars var1, String var2, ItemStack var3, String var4, ArrayList var5, ItemStack[] var6, ArrayList var7, Enums.Rarity var8, int var9) {
      this.name = var2;
      this.permission = var4.equalsIgnoreCase("none") ? "" : var4;
      this.items = var5;
      this.armor = var6;
      this.rarity = var8;
      this.value = var9;
      ItemStackBuilder var10 = (new ItemStackBuilder(var3)).setName(var8.getColor() + var2);
      var10.addLore(" ", (String)var1.customization.lores.get("Description"));
      Iterator var12 = var7.iterator();

      while(var12.hasNext()) {
         String var11 = (String)var12.next();
         var10.addLore(var11);
      }

      var10.addLore(" ", ((String)var1.customization.lores.get("Rarity")).replace("%rarity%", var8.toString()), ((String)var1.customization.lores.get("Value")).replace("%price%", String.valueOf(var9)));
      var10.addLore(" ", (String)var1.customization.lores.get("Armor"));
      String[] var14 = new String[]{"Boots", "Leggings", "Chestplate", "Helmet"};

      for(int var15 = var14.length - 1; var15 > -1; --var15) {
         var10.addLore(((String)var1.customization.lores.get(var14[var15])).replace("%" + var14[var15].toLowerCase() + "%", var6[var15].getType().equals(Material.AIR) ? (String)var1.customization.lores.get("No-Items") : var6[var15].getType().name().replace("_", " ")));
      }

      var10.addLore(" ", (String)var1.customization.lores.get("Items"));
      Iterator var13 = var5.iterator();

      while(var13.hasNext()) {
         ItemStack var16 = (ItemStack)var13.next();
         var10.addLore(((String)var1.customization.lores.get("Item")).replace("%item%", var16.getType().name() + " x" + var16.getAmount()));
      }

      if (var5.isEmpty()) {
         var10.addLore((String)var1.customization.lores.get("No-Items"));
      }

      if (!this.permission.isEmpty()) {
         var10.addLore(" ", (String)var1.customization.lores.get("Permission-Needed"));
      }

      var10.addLore(" ", ((String)var1.customization.lores.get("Selling")).replace("%coins%", String.valueOf((int)(var1.config.selling_value * (double)var9))));
      this.item = var10.build();
   }

   public void apply(Player var1) {
      Iterator var3 = this.items.iterator();

      while(var3.hasNext()) {
         ItemStack var2 = (ItemStack)var3.next();
         var1.getInventory().addItem(new ItemStack[]{var2});
      }

      var1.getInventory().setArmorContents(this.armor);
   }
}
