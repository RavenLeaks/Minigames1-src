package me.wazup.kitbattle;

import org.bukkit.Location;

public class Cuboid {
   private int x1;
   private int y1;
   private int z1;
   private int x2;
   private int y2;
   private int z2;
   public String worldName;

   public Cuboid(Location var1, Location var2) {
      this.worldName = var1.getWorld().getName();
      this.x1 = Math.min(var1.getBlockX(), var2.getBlockX());
      this.y1 = Math.min(var1.getBlockY(), var2.getBlockY());
      this.z1 = Math.min(var1.getBlockZ(), var2.getBlockZ());
      this.x2 = Math.max(var1.getBlockX(), var2.getBlockX());
      this.y2 = Math.max(var1.getBlockY(), var2.getBlockY());
      this.z2 = Math.max(var1.getBlockZ(), var2.getBlockZ());
   }

   public Cuboid(String var1) {
      String[] var2 = var1.split(", ");
      this.worldName = var2[0];
      this.x1 = Integer.parseInt(var2[1]);
      this.y1 = Integer.parseInt(var2[2]);
      this.z1 = Integer.parseInt(var2[3]);
      this.x2 = Integer.parseInt(var2[4]);
      this.y2 = Integer.parseInt(var2[5]);
      this.z2 = Integer.parseInt(var2[6]);
   }

   public boolean contains(Location var1) {
      return var1.getWorld().getName().equals(this.worldName) && var1.getBlockX() >= this.x1 && var1.getBlockX() <= this.x2 && var1.getBlockY() > this.y1 && var1.getBlockY() < this.y2 && var1.getBlockZ() >= this.z1 && var1.getBlockZ() <= this.z2;
   }

   public int getSize() {
      return (this.x2 - this.x1 + 1) * (this.y2 - this.y1 + 1) * (this.z2 - this.z1 + 1);
   }

   public String toString() {
      return this.worldName + ", " + this.x1 + ", " + this.y1 + ", " + this.z1 + ", " + this.x2 + ", " + this.y2 + ", " + this.z2;
   }
}
