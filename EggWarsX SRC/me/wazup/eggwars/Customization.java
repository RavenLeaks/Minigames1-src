package me.wazup.eggwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

class Customization {
   String prefix;
   public HashMap messages;
   public HashMap inventories;
   public HashMap titles;
   public HashMap lores;
   public HashMap deathMessages;
   public List killMessages;
   public String player_suicide;
   public HashMap scoreboard;
   String scoreboard_title;
   String scoreboard_header;
   String scoreboard_footer;
   String signs_title;
   String signs_leave;
   String signs_autojoin;
   String signs_join;
   ChatColor signs_arena_color;

   public Customization(FileConfiguration var1) {
      this.prefix = this.c(var1.getString("prefix"));
      this.messages = new HashMap();
      Iterator var3 = var1.getConfigurationSection("Messages").getKeys(false).iterator();

      String var2;
      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.messages.put(var2, this.prefix + this.c(var1.getString("Messages." + var2)));
      }

      this.deathMessages = new HashMap();
      var3 = var1.getConfigurationSection("Death-Messages").getKeys(false).iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         if (!var2.equalsIgnoreCase("PLAYER")) {
            this.deathMessages.put(var2.toUpperCase(), this.prefix + this.c(var1.getString("Death-Messages." + var2)));
         }
      }

      this.killMessages = new ArrayList();
      var3 = var1.getStringList("Death-Messages.PLAYER.OTHER").iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.killMessages.add(this.prefix + this.c(var2));
      }

      this.player_suicide = this.prefix + this.c(var1.getString("Death-Messages.PLAYER.SUICIDE"));
      this.scoreboard = new HashMap();
      var3 = var1.getConfigurationSection("Scoreboard.Tags").getKeys(false).iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.scoreboard.put(var2, this.c(var1.getString("Scoreboard.Tags." + var2)));
      }

      this.scoreboard_title = this.c(var1.getString("Scoreboard.Title").replace("%pointedstar%", String.valueOf(Enums.SPECIAL_CHARACTER.POINTED_STAR)));
      this.scoreboard_header = this.c(var1.getString("Scoreboard.Header"));
      this.scoreboard_footer = this.c(var1.getString("Scoreboard.Footer"));
      this.inventories = new HashMap();
      var3 = var1.getConfigurationSection("Inventories").getKeys(false).iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.inventories.put(var2, this.c(var1.getString("Inventories." + var2)));
      }

      this.titles = new HashMap();
      var3 = var1.getConfigurationSection("Titles").getKeys(false).iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.titles.put(var2, this.c(var1.getString("Titles." + var2)));
      }

      this.lores = new HashMap();
      var3 = var1.getConfigurationSection("Lores").getKeys(false).iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.lores.put(var2, this.c(var1.getString("Lores." + var2)));
      }

      Enums.ArenaState[] var5;
      int var4 = (var5 = Enums.ArenaState.values()).length;

      for(int var7 = 0; var7 < var4; ++var7) {
         Enums.ArenaState var6 = var5[var7];
         var6.value = this.c(var1.getString("States." + var6.name()));
      }

      this.signs_title = this.c(var1.getString("Signs.Title"));
      this.signs_leave = this.c(var1.getString("Signs.Leave"));
      this.signs_autojoin = this.c(var1.getString("Signs.Autojoin"));
      this.signs_join = this.c(var1.getString("Signs.Join"));
      this.signs_arena_color = ChatColor.getByChar(var1.getString("Signs.Arena-Color"));
   }

   private String c(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }
}
