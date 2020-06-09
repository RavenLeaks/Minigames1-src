package me.wazup.skywars;

import org.bukkit.inventory.ItemStack;

class Trail extends Sellable {
   int typeId;

   public Trail(Skywars var1, String var2, String var3, int var4, Enums.Rarity var5, ItemStack var6) {
      this.name = var2;
      this.permission = var3.equalsIgnoreCase("none") ? "" : var3;
      this.typeId = var6.getTypeId();
      this.value = var4;
      this.rarity = var5;
      ItemStackBuilder var7 = (new ItemStackBuilder(var6)).setName(var5.getColor() + var2);
      var7.addLore(" ", ((String)var1.customization.lores.get("Rarity")).replace("%rarity%", var5.toString()), ((String)var1.customization.lores.get("Value")).replace("%price%", String.valueOf(var4)));
      if (!this.permission.isEmpty()) {
         var7.addLore(" ", (String)var1.customization.lores.get("Permission-Needed"));
      }

      var7.addLore(" ", ((String)var1.customization.lores.get("Selling")).replace("%coins%", String.valueOf((int)(var1.config.selling_value * (double)var4))));
      this.item = var7.build();
   }
}
