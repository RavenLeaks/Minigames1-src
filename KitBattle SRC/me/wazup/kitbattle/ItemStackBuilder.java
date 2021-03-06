package me.wazup.kitbattle;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class ItemStackBuilder {
   private ItemStack item;
   private ItemMeta meta;
   private List lore;

   public ItemStackBuilder(ItemStack var1) {
      this.item = var1;
      this.meta = var1.getItemMeta();
      this.lore = (List)(this.meta != null && this.meta.hasLore() ? this.meta.getLore() : new ArrayList());
   }

   public ItemStackBuilder(Material var1) {
      this(new ItemStack(var1));
   }

   public ItemStackBuilder setType(Material var1) {
      this.item.setType(var1);
      return this;
   }

   public ItemStackBuilder setName(String var1) {
      this.meta.setDisplayName(var1);
      return this;
   }

   public ItemStackBuilder addLore(String... var1) {
      String[] var5 = var1;
      int var4 = var1.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         String var2 = var5[var3];
         this.lore.add(var2);
      }

      return this;
   }

   public ItemStackBuilder addEnchantment(Enchantment var1, int var2) {
      this.meta.addEnchant(var1, var2, true);
      return this;
   }

   public ItemStackBuilder setDurability(int var1) {
      if (Kitbattle.setDamageMethod != null) {
         try {
            Kitbattle.setDamageMethod.invoke(this.meta, (short)var1);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      } else {
         this.item.setDurability((short)var1);
      }

      return this;
   }

   public ItemStackBuilder setAmount(int var1) {
      this.item.setAmount(var1);
      return this;
   }

   public ItemStackBuilder clearLore() {
      this.lore.clear();
      return this;
   }

   public ItemStackBuilder removeLastLore() {
      this.lore.remove(this.lore.size() - 1);
      return this;
   }

   public void setColor(Color var1) {
      ((LeatherArmorMeta)this.meta).setColor(var1);
   }

   public void setPotionEffect(PotionType var1, boolean var2, boolean var3) {
      PotionMeta var4 = (PotionMeta)this.item.getItemMeta();

      try {
         PotionMeta.class.getMethod("setBasePotionData", Class.forName("org.bukkit.potion.PotionData")).invoke(var4, Class.forName("org.bukkit.potion.PotionData").getConstructor(PotionType.class, Boolean.TYPE, Boolean.TYPE).newInstance(var1, var2, var3));
      } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException var6) {
         Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[KitBattle] The used format for potions does not work on this minecraft version, please try other format which is POTION:DURABILITY");
      }

      this.meta = var4;
   }

   public ItemStackBuilder replaceLore(String var1, String var2) {
      for(int var3 = 0; var3 < this.lore.size(); ++var3) {
         if (((String)this.lore.get(var3)).contains(var1)) {
            this.lore.remove(var3);
            this.lore.add(var3, var2);
            break;
         }
      }

      return this;
   }

   public ItemStack build() {
      if (this.meta != null) {
         this.meta.setLore(this.lore);
      }

      this.lore.clear();
      this.item.setItemMeta(this.meta);
      return this.item;
   }
}
