package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
   private ItemStack originalLogo;
   private ItemStack logo;
   private ItemStack shopLogo;
   private ItemStack[] items;
   private ItemStack[] armor;
   Collection effects;
   ArrayList abilities;
   boolean enabled;
   boolean requirePermission;
   private int Price;
   String name;
   String permission;
   Inventory kitPreview;
   Kit original;
   private Kitbattle plugin;

   public Kit(Kitbattle var1, String var2, ItemStack var3, ItemStack[] var4, ItemStack[] var5, int var6, Collection var7, ArrayList var8, boolean var9, boolean var10) {
      this.plugin = var1;
      this.originalLogo = var3;
      this.items = var4;
      this.armor = var5;
      this.Price = var6;
      this.name = var2;
      this.effects = var7;
      this.abilities = var8;
      this.enabled = var9;
      this.requirePermission = var10;
      this.permission = "kitbattle.kits." + var2;
      this.generateLogos();
      this.kitPreview = Bukkit.createInventory((InventoryHolder)null, 45, (String)var1.msgs.inventories.get("Kit-Preview") + var2);

      int var11;
      for(var11 = 0; var11 < var4.length; ++var11) {
         if (var4[var11] != null && !var4[var11].getType().equals(Material.AIR)) {
            this.kitPreview.setItem(var11 + 9 >= 45 ? 44 : var11 + 9, var4[var11]);
         }
      }

      for(var11 = 4; var11 < 8; ++var11) {
         this.kitPreview.setItem(var11, var1.pane_itemstack);
      }

      for(var11 = 0; var11 < var5.length; ++var11) {
         if (var5[var11] != null) {
            this.kitPreview.addItem(new ItemStack[]{var5[var11]});
         }
      }

      this.kitPreview.setItem(8, var1.back_itemstack);
   }

   public ItemStack getLogo() {
      return this.logo;
   }

   public ItemStack getShopLogo() {
      return this.shopLogo;
   }

   public void generateLogos() {
      this.logo = this.originalLogo.clone();
      this.shopLogo = this.logo.clone();
      ItemMeta var1 = this.logo.getItemMeta();
      Object var2 = new ArrayList();
      if (var1.getLore() != null) {
         var2 = var1.getLore();
      }

      Iterator var4 = this.plugin.config.kitLoresOwned.iterator();

      while(var4.hasNext()) {
         String var3 = (String)var4.next();
         ((List)var2).add(ChatColor.translateAlternateColorCodes('&', var3.replace("%statecolor%", this.enabled ? ChatColor.GREEN.toString() : ChatColor.RED.toString()).replace("%state%", this.enabled ? "Enabled" : "Disabled").replace("%permissioncolor%", this.requirePermission ? ChatColor.RED.toString() : ChatColor.GREEN.toString()).replace("%requirespermission%", this.requirePermission ? "Yes" : "No").replace("%price%", String.valueOf(this.Price))));
      }

      var1.setLore((List)var2);
      this.logo.setItemMeta(var1);
      ItemMeta var7 = this.shopLogo.getItemMeta();
      var7.setDisplayName(ChatColor.LIGHT_PURPLE + this.name);
      Object var8 = new ArrayList();
      if (var7.getLore() != null) {
         var8 = var7.getLore();
      }

      Iterator var6 = this.plugin.config.kitLoresShop.iterator();

      while(var6.hasNext()) {
         String var5 = (String)var6.next();
         ((List)var8).add(ChatColor.translateAlternateColorCodes('&', var5.replace("%statecolor%", this.enabled ? ChatColor.GREEN.toString() : ChatColor.RED.toString()).replace("%state%", this.enabled ? "Enabled" : "Disabled").replace("%permissioncolor%", this.requirePermission ? ChatColor.RED.toString() : ChatColor.GREEN.toString()).replace("%requirespermission%", this.requirePermission ? "Yes" : "No").replace("%price%", String.valueOf(this.Price))));
      }

      var7.setLore((List)var8);
      this.shopLogo.setItemMeta(var7);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public ItemStack[] getItems() {
      return this.items;
   }

   public ItemStack[] getArmor() {
      return this.armor;
   }

   public ArrayList getAbilities() {
      return this.abilities;
   }

   public void giveItems(Player var1) {
      var1.getInventory().clear();
      var1.getInventory().setArmorContents((ItemStack[])null);
      var1.getInventory().setContents(this.items);
      var1.getInventory().setArmorContents(this.armor);
      var1.addPotionEffects(this.effects);
      PlayerData var2 = (PlayerData)this.plugin.playerData.get(var1.getName());
      if (var2.deathstreak >= this.plugin.config.leastDeathstreak) {
         int var3 = this.plugin.config.leastDeathstreak;
         Iterator var5 = this.plugin.config.Deathstreaks.keySet().iterator();

         while(var5.hasNext()) {
            int var4 = (Integer)var5.next();
            if (var2.deathstreak >= var4) {
               var3 = var4;
            }
         }

         var5 = ((List)this.plugin.config.Deathstreaks.get(var3)).iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var6.replace("%player%", var1.getName()));
         }
      }

   }

   public int getPrice() {
      return this.Price;
   }

   public int getTotalPrice() {
      int var1 = this.Price;

      for(Kit var2 = this.original; var2 != null; var2 = var2.original) {
         var1 += var2.getPrice();
      }

      return var1;
   }
}
