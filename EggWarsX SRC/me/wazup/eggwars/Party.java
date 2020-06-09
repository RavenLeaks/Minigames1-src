package me.wazup.eggwars;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

class Party {
   String leaderName;
   Enums.PartyPrivacy privacy;
   int capacity;
   ArrayList players = new ArrayList();
   ArrayList invited = new ArrayList();
   Inventory gui;
   Inventory playersInventory;
   int id;
   int slot;
   private Eggwars plugin;

   public Party(Eggwars var1, Player var2, int var3) {
      this.plugin = var1;
      this.capacity = var3;
      this.players.add(var2.getName());
      this.gui = Bukkit.createInventory((InventoryHolder)null, 9, (String)var1.customization.inventories.get("Party-Settings"));
      var1.cageInventory(this.gui, true);
      this.playersInventory = Bukkit.createInventory((InventoryHolder)null, var1.getInventorySize(var3 + 1), (String)var1.customization.inventories.get("Party-Players"));

      for(int var4 = var3; var4 < this.playersInventory.getSize(); ++var4) {
         this.playersInventory.setItem(var4, var1.pane_itemstack);
      }

      this.playersInventory.setItem(this.playersInventory.getSize() - 1, var1.back_itemstack);
      this.setLeader(var2);
      this.setPrivacy(Enums.PartyPrivacy.INVITE);
      this.updatePlayers();
      this.gui.setItem(6, (new ItemStackBuilder(Material.ENDER_CHEST)).setName(ChatColor.GREEN + "Invite").addLore(ChatColor.GRAY + "Click to invite players").build());
      this.gui.setItem(8, (new ItemStackBuilder(Material.TNT)).setName(ChatColor.RED + "Leave").addLore(ChatColor.GRAY + "Click to leave the party").build());
   }

   public void setLeader(Player var1) {
      this.leaderName = var1.getName();
      this.gui.setItem(0, (new ItemStackBuilder(Material.PAPER)).setName(ChatColor.YELLOW + "Leader:").addLore(ChatColor.GOLD + var1.getName()).build());
   }

   public void setPrivacy(Enums.PartyPrivacy var1) {
      this.privacy = var1;
      this.gui.setItem(2, (new ItemStackBuilder(Material.EYE_OF_ENDER)).setName(ChatColor.AQUA + "Privacy:").addLore(var1.toString()).build());
   }

   public void updatePlayers() {
      ItemStackBuilder var1 = (new ItemStackBuilder(Material.CHEST)).setName(ChatColor.LIGHT_PURPLE + "Players:").addLore("" + ChatColor.LIGHT_PURPLE + this.players.size() + ChatColor.YELLOW + "/" + ChatColor.LIGHT_PURPLE + this.capacity, " ");
      int var2 = 0;

      for(int var3 = 0; var3 < this.capacity; ++var3) {
         this.playersInventory.setItem(var3, new ItemStack(Material.AIR));
      }

      for(Iterator var4 = this.players.iterator(); var4.hasNext(); ++var2) {
         String var7 = (String)var4.next();
         ChatColor var5 = var7.equals(this.leaderName) ? ChatColor.GOLD : ChatColor.GRAY;
         var1.addLore(ChatColor.GRAY + "- " + var5 + var7);
         ItemStack var6 = this.plugin.getSkull(var7, var5 + var7);
         if (!var7.equals(this.leaderName)) {
            (new ItemStackBuilder(var6)).addLore(ChatColor.YELLOW + "Left click to promote to leader!", " ", ChatColor.YELLOW + "Right click to kick!").build();
         }

         this.playersInventory.setItem(var2, var6);
      }

      this.gui.setItem(4, var1.build());
   }

   public void updateItem() {
      this.plugin.partySelector.setItem(this.id, this.slot, (new ItemStackBuilder(Material.BOOK)).setName(ChatColor.GOLD + this.leaderName + "'s party").addLore(ChatColor.AQUA + "Privacy: " + this.privacy, " ", ChatColor.YELLOW + "Players: " + ChatColor.LIGHT_PURPLE + this.players.size() + ChatColor.YELLOW + "/" + ChatColor.LIGHT_PURPLE + this.capacity).build());
   }

   public void sendMessage(String var1) {
      Iterator var3 = this.plugin.getPlayers(this.players).iterator();

      while(var3.hasNext()) {
         Player var2 = (Player)var3.next();
         var2.sendMessage(var1);
      }

   }

   public void leave(Player var1) {
      this.players.remove(var1.getName());
      ((PlayerData)this.plugin.playerData.get(var1.getName())).party = null;
      var1.closeInventory();
      var1.sendMessage((String)this.plugin.customization.messages.get("Party-Player-Leave"));
      if (this.players.isEmpty()) {
         this.plugin.parties.remove(this);
         this.plugin.updatePartiesInventory();
      } else {
         this.sendMessage(((String)this.plugin.customization.messages.get("Party-Player-Leave-Alert")).replace("%player%", var1.getName()));
         if (var1.getName().equals(this.leaderName)) {
            Player var2 = Bukkit.getPlayer((String)this.players.get(0));
            this.setLeader(var2);
            this.sendMessage(((String)this.plugin.customization.messages.get("Party-New-Leader")).replace("%player%", var2.getName()));
         }

         this.updateItem();
         this.updatePlayers();
      }

   }

   public void join(Player var1) {
      if (this.players.size() >= this.capacity) {
         var1.sendMessage((String)this.plugin.customization.messages.get("Party-Full"));
      } else {
         if (!this.privacy.equals(Enums.PartyPrivacy.PUBLIC) && !this.invited.contains(var1.getName())) {
            var1.sendMessage((String)this.plugin.customization.messages.get("Party-Not-Invited"));
         } else {
            this.players.add(var1.getName());
            ((PlayerData)this.plugin.playerData.get(var1.getName())).party = this;
            this.updatePlayers();
            this.updateItem();
            this.sendMessage(((String)this.plugin.customization.messages.get("Party-Join")).replace("%player%", var1.getName()));
            var1.closeInventory();
         }

      }
   }

   public void invite(final Player var1) {
      this.sendMessage(((String)this.plugin.customization.messages.get("Party-Invite-Send")).replace("%player%", this.leaderName).replace("%target%", var1.getName()));
      this.invited.add(var1.getName());
      var1.sendMessage(((String)this.plugin.customization.messages.get("Party-Invite-Receive")).replace("%leader%", this.leaderName).replace("%seconds%", String.valueOf(this.plugin.config.partyInvitationLength)));
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
         public void run() {
            Party.this.invited.remove(var1.getName());
            if (!Party.this.players.contains(var1.getName())) {
               var1.sendMessage((String)Party.this.plugin.customization.messages.get("Party-Invitation-Expire"));
            }

         }
      }, (long)(this.plugin.config.partyInvitationLength * 20));
   }

   public void kick(Player var1) {
      var1.sendMessage((String)this.plugin.customization.messages.get("Party-Player-Kick"));
      this.leave(var1);
      this.sendMessage(((String)this.plugin.customization.messages.get("Party-Player-Kick-Alert")).replace("%leader%", this.leaderName).replace("%player%", var1.getName()));
   }
}
