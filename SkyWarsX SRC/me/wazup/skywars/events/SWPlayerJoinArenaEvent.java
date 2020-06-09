package me.wazup.skywars.events;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SWPlayerJoinArenaEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   Player p;
   String arenaName;
   String arenaWorldName;
   List arenaPlayers;

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public SWPlayerJoinArenaEvent(Player var1, String var2, String var3, List var4) {
      this.p = var1;
      this.arenaName = var2;
      this.arenaWorldName = var3;
      this.arenaPlayers = var4;
   }

   public Player getPlayer() {
      return this.p;
   }

   public String getArenaName() {
      return this.arenaName;
   }

   public World getArenaWorld() {
      return Bukkit.getWorld(this.arenaWorldName);
   }

   public List getArenaPlayers() {
      return this.arenaPlayers;
   }
}
