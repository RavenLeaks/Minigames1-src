package me.wazup.eggwars;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class Kit extends Sellable {
   ArrayList items;
   ItemStack[] armor;

   public Kit(Eggwars var1, String var2, ItemStack var3, String var4, ArrayList var5, ItemStack[] var6, ArrayList var7, Enums.Rarity var8, int var9) {
      this.name = var2;
      this.permission = var4.equalsIgnoreCase("none") ? "" : var4;
      this.items = var5;
      this.armor = var6;
      this.rarity = var8;
      this.value = var9;
      ItemStackBuilder var10 = (new ItemStackBuilder(var3)).setName(var8.getColor() + var2);
      ChatColor var11 = ChatColor.LIGHT_PURPLE;
      var10.addLore(" ", (String)var1.customization.lores.get("Description"));
      Iterator var13 = var7.iterator();

      while(var13.hasNext()) {
         String var12 = (String)var13.next();
         var10.addLore(var12);
      }

      var10.addLore(" ", ((String)var1.customization.lores.get("Rarity")).replace("%rarity%", var8.toString()), ((String)var1.customization.lores.get("Value")).replace("%price%", String.valueOf(var9)));
      var10.addLore(" ", (String)var1.customization.lores.get("Armor"));
      String[] var15 = new String[]{"Boots", "Leggings", "Chestplate", "Helmet"};

      for(int var16 = var15.length - 1; var16 > -1; --var16) {
         var10.addLore(((String)var1.customization.lores.get(var15[var16])).replace("%" + var15[var16].toLowerCase() + "%", var6[var16].getType().equals(Material.AIR) ? "NONE" : var6[var16].getType().name().replace("_", " ")));
      }

      var10.addLore(" ", (String)var1.customization.lores.get("Items"));
      Iterator var14 = var5.iterator();

      while(var14.hasNext()) {
         ItemStack var17 = (ItemStack)var14.next();
         var10.addLore(((String)var1.customization.lores.get("Item")).replace("%item%", var17.getType().name() + " x" + var17.getAmount()));
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
      var1.updateInventory();
   }
}
