package me.wazup.skywars;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class ChestType {
   String name;
   ArrayList items;
   int minItems;
   int maxItems;
   SmartInventory editor;

   public ChestType(Skywars var1, String var2, ArrayList var3, int var4, int var5) {
      this.name = var2;
      this.items = var3;
      this.minItems = var4;
      this.maxItems = var5;
      this.editor = new SmartInventory(var1, ChatColor.BLUE + "Editing");
      int var6 = 0;

      for(int var7 = 1; (double)var7 < Math.ceil(Double.valueOf((double)(var3.isEmpty() ? 1 : var3.size())) / (double)var1.smartSlots.length) + 1.0D; ++var7) {
         int var8 = this.editor.addInventory(ChatColor.RED + var2 + " #" + var7);
         int[] var12;
         int var11 = (var12 = var1.smartSlots).length;

         for(int var10 = 0; var10 < var11; ++var10) {
            int var9 = var12[var10];
            if (var6 >= var3.size()) {
               break;
            }

            this.editor.setItem(var8, var9, (ItemStack)var3.get(var6));
            ++var6;
         }

         this.addSettings(var1, var8);
      }

   }

   public void addSettings(Skywars var1, int var2) {
      this.editor.setItem(var2, 46, var1.minus_itemstack);
      this.editor.setItem(var2, 47, (new ItemStackBuilder(Material.PAPER)).setName(ChatColor.YELLOW + "Min items: " + ChatColor.GOLD + this.minItems).build());
      this.editor.setItem(var2, 48, var1.plus_itemstack);
      this.editor.setItem(var2, 49, var1.save_itemstack);
      this.editor.setItem(var2, 50, var1.minus_itemstack);
      this.editor.setItem(var2, 51, (new ItemStackBuilder(Material.PAPER)).setName(ChatColor.YELLOW + "Max items: " + ChatColor.GOLD + this.maxItems).build());
      this.editor.setItem(var2, 52, var1.plus_itemstack);
      this.editor.setItem(var2, 53, (new ItemStackBuilder(Material.EMERALD)).setName(ChatColor.GREEN + "Generate").addLore(ChatColor.GRAY + "Click to generate a new page!").build());
   }
}
