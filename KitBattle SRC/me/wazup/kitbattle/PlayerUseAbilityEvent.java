package me.wazup.kitbattle;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUseAbilityEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   Ability a;
   Player p;

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public PlayerUseAbilityEvent(Player var1, Ability var2) {
      this.p = var1;
      this.a = var2;
   }

   public Player getPlayer() {
      return this.p;
   }

   public Ability getAbility() {
      return this.a;
   }
}
