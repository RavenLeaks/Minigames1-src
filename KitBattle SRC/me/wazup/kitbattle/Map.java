package me.wazup.kitbattle;

import java.util.List;
import org.bukkit.Location;

public abstract class Map {
   Kitbattle plugin;
   String name;
   List spawnpoints;
   boolean enabled;

   public Map(Kitbattle var1, String var2, List var3, boolean var4) {
      this.plugin = var1;
      this.name = var2;
      this.spawnpoints = var3;
      this.enabled = var4;
   }

   public Location getSpawnpoint() {
      return this.spawnpoints != null && !this.spawnpoints.isEmpty() ? (Location)this.spawnpoints.get(this.plugin.random.nextInt(this.spawnpoints.size())) : null;
   }

   public boolean isAvailable() {
      return this.enabled && !this.spawnpoints.isEmpty();
   }
}
