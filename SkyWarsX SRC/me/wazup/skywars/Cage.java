package me.wazup.skywars;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

class Cage extends Sellable {
   ItemStack[] parts = new ItemStack[5];

   public Cage(Skywars var1, String var2, ItemStack var3, String var4, Enums.Rarity var5, int var6, ItemStack[] var7) {
      this.name = var2;
      this.permission = var4.equalsIgnoreCase("none") ? "" : var4;
      this.rarity = var5;
      this.value = var6;
      this.parts = var7;
      ItemStackBuilder var8 = new ItemStackBuilder(var3);
      var8.setName(var5.getColor() + var2);
      var8.addLore(" ", ((String)var1.customization.lores.get("Rarity")).replace("%rarity%", var5.toString()), ((String)var1.customization.lores.get("Value")).replace("%price%", String.valueOf(var6)));
      if (!this.permission.isEmpty()) {
         var8.addLore(" ", (String)var1.customization.lores.get("Permission-Needed"));
      }

      var8.addLore(" ", ((String)var1.customization.lores.get("Selling")).replace("%coins%", String.valueOf((int)(var1.config.selling_value * (double)var6))));
      this.item = var8.build();
   }

   public void build(HashMap var1) {
      Iterator var3 = var1.keySet().iterator();

      while(var3.hasNext()) {
         Location var2 = (Location)var3.next();
         var2.getBlock().setTypeIdAndData(this.parts[(Integer)var1.get(var2)].getTypeId(), this.parts[(Integer)var1.get(var2)].getData().getData(), true);
      }

   }
}
