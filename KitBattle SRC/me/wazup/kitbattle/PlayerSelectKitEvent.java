package me.wazup.kitbattle;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSelectKitEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   Kit k;
   Player p;

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public PlayerSelectKitEvent(Player var1, Kit var2) {
      this.p = var1;
      this.k = var2;
   }

   public Player getPlayer() {
      return this.p;
   }

   public Kit getKit() {
      return this.k;
   }
}
