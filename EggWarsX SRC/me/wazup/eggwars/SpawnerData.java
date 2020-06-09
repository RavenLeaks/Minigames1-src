package me.wazup.eggwars;

import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

public class SpawnerData {
   int itemLimit;
   ItemStack fixCost;
   HashMap upgrades;

   public SpawnerData(int var1, ItemStack var2, HashMap var3) {
      this.itemLimit = var1;
      this.fixCost = var2;
      this.upgrades = var3;
   }
}
