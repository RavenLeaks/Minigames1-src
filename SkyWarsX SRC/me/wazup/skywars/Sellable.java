package me.wazup.skywars;

import org.bukkit.inventory.ItemStack;

abstract class Sellable {
   String name;
   ItemStack item;
   int value;
   String permission;
   Enums.Rarity rarity;
}
