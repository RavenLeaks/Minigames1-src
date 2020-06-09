package me.wazup.kitbattle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class FileManager {
   private HashMap configurations = new HashMap();
   private Kitbattle plugin;
   List armorParts = Arrays.asList("Boots", "Leggings", "Chestplate", "Helmet");

   public FileManager(Kitbattle var1) {
      this.plugin = var1;
      var1.reloadConfig();
      var1.getConfig().options().copyDefaults(true);
      var1.saveConfig();
      this.registerConfig("players.yml");
      this.registerConfig("maps.yml");
      this.registerConfig("kits.yml");
      this.registerConfig("ranks.yml");
      this.registerConfig("messages.yml");
      this.registerConfig("abilities.yml");
      this.registerConfig("signs.yml");
      this.registerConfig("shop.yml");
      this.registerConfig("achievements.yml");
      this.registerConfig("trails_blacklist.yml");
      Iterator var3 = this.configurations.keySet().iterator();

      while(var3.hasNext()) {
         String var2 = (String)var3.next();
         this.reloadConfig(var2);
         ((FileConfiguration)this.configurations.get(var2)).options().copyDefaults(true);
         this.saveConfig(var2);
      }

   }

   private void registerConfig(String var1) {
      this.configurations.put(var1, YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), var1)));
   }

   public FileConfiguration getConfig(String var1) {
      return (FileConfiguration)this.configurations.get(var1);
   }

   private void reloadConfig(String var1) {
      InputStream var2 = this.plugin.getResource(var1);
      if (var2 != null) {
         InputStreamReader var3 = new InputStreamReader(var2);
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         ((FileConfiguration)this.configurations.get(var1)).setDefaults(var4);

         try {
            var3.close();
            var2.close();
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

   }

   public void saveConfig(String var1) {
      try {
         ((FileConfiguration)this.configurations.get(var1)).save(new File(this.plugin.getDataFolder(), var1));
      } catch (IOException var3) {
         Bukkit.getConsoleSender().sendMessage(this.plugin.kb + "Couldn't save " + var1 + "!");
      }

   }

   public void setupKits() {
      FileConfiguration var1 = this.getConfig("kits.yml");
      ArrayList var2;
      if (this.plugin.getConfig().getBoolean("Create-Default-Kits")) {
         this.plugin.getConfig().set("Create-Default-Kits", false);
         this.plugin.saveConfig();
         if (!var1.contains("Kits.Pvp")) {
            var1.set("Kits.Pvp.Enabled", true);
            var1.set("Kits.Pvp.Require-Permission", false);
            var1.set("Kits.Pvp.Item", "DIAMOND_SWORD");
            var1.set("Kits.Pvp.Price", 0);
            var1.set("Kits.Pvp.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Pvp.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Pvp.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Pvp.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Pvp.Items", var2);
            var1.set("Kits.Pvp.Abilities", new ArrayList());
            var1.set("Kits.Pvp.Potion-Effects", Arrays.asList());
            var1.set("Kits.Pvp.Description", Arrays.asList("&bA very basic but strong kit!"));
         }

         ArrayList var3;
         if (!var1.contains("Kits.Archer")) {
            var1.set("Kits.Archer.Enabled", true);
            var1.set("Kits.Archer.Require-Permission", false);
            var1.set("Kits.Archer.Item", "BOW");
            var1.set("Kits.Archer.Price", 100);
            var1.set("Kits.Archer.Armor.Helmet", "LEATHER_HELMET : 1");
            var1.set("Kits.Archer.Armor.Chestplate", "LEATHER_CHESTPLATE : 1");
            var1.set("Kits.Archer.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Archer.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BOW : 1 : enchant:ARROW_DAMAGE:1");
            var2.add("ARROW : 64");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Archer.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Archer.Abilities", var3);
            var1.set("Kits.Archer.Potion-Effects", Arrays.asList());
            var1.set("Kits.Archer.Description", Arrays.asList("&bStart with a sword and a strong bow", "&band a stack of arrows"));
         }

         if (!var1.contains("Kits.Ninja")) {
            var1.set("Kits.Ninja.Enabled", true);
            var1.set("Kits.Ninja.Require-Permission", false);
            var1.set("Kits.Ninja.Item", "NETHER_STAR");
            var1.set("Kits.Ninja.Price", 200);
            var1.set("Kits.Ninja.Armor.Helmet", "LEATHER_HELMET : 1 : dye:BLACK");
            var1.set("Kits.Ninja.Armor.Chestplate", "CHAINMAIL_CHESTPLATE : 1");
            var1.set("Kits.Ninja.Armor.Leggings", "CHAINMAIL_LEGGINGS : 1");
            var1.set("Kits.Ninja.Armor.Boots", "DIAMOND_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Ninja.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Ninja.Abilities", var3);
            var1.set("Kits.Ninja.Potion-Effects", Arrays.asList("SPEED : 999999 : 2"));
            var1.set("Kits.Ninja.Description", Arrays.asList("&bStart with a diamond sword", "&band week armor", "&bAnd infinite speed 2 potion"));
         }

         if (!var1.contains("Kits.Tank")) {
            var1.set("Kits.Tank.Enabled", true);
            var1.set("Kits.Tank.Require-Permission", false);
            var1.set("Kits.Tank.Item", "DIAMOND_CHESTPLATE");
            var1.set("Kits.Tank.Price", 400);
            var1.set("Kits.Tank.Armor.Helmet", "DIAMOND_HELMET : 1");
            var1.set("Kits.Tank.Armor.Chestplate", "DIAMOND_CHESTPLATE : 1");
            var1.set("Kits.Tank.Armor.Leggings", "DIAMOND_LEGGINGS : 1");
            var1.set("Kits.Tank.Armor.Boots", "DIAMOND_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("IRON_SWORD : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Tank.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Tank.Abilities", var3);
            var1.set("Kits.Tank.Potion-Effects", Arrays.asList("SLOW : 999999 : 2"));
            var1.set("Kits.Tank.Description", Arrays.asList("&bStart with diamond armor", "&band an iron sword", "&bbut a slowness effect"));
         }

         if (!var1.contains("Kits.Pyro")) {
            var1.set("Kits.Pyro.Enabled", true);
            var1.set("Kits.Pyro.Require-Permission", false);
            var1.set("Kits.Pyro.Item", "FLINT_AND_STEEL");
            var1.set("Kits.Pyro.Price", 500);
            var1.set("Kits.Pyro.Armor.Helmet", "LEATHER_HELMET : 1 : enchant:PROTECTION_FIRE:5");
            var1.set("Kits.Pyro.Armor.Chestplate", "IRON_CHESTPLATE : 1 : enchant:PROTECTION_FIRE:5");
            var1.set("Kits.Pyro.Armor.Leggings", "LEATHER_LEGGINGS : 1 : enchant:PROTECTION_FIRE:5");
            var1.set("Kits.Pyro.Armor.Boots", "IRON_BOOTS : 1 : enchant:PROTECTION_FIRE:5");
            var2 = new ArrayList();
            var2.add("IRON_SWORD : 1 : enchant:FIRE_ASPECT:1");
            var2.add("BOW : 1 : enchant:ARROW_FIRE:1");
            var2.add("ARROW : 64");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Pyro.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Pyro.Abilities", var3);
            var1.set("Kits.Pyro.Potion-Effects", Arrays.asList());
            var1.set("Kits.Pyro.Description", Arrays.asList("&bStart with a fiery sword", "&band a fiery bow", "&band fire protection armor"));
         }

         if (!var1.contains("Kits.Assassin")) {
            var1.set("Kits.Assassin.Enabled", true);
            var1.set("Kits.Assassin.Require-Permission", false);
            var1.set("Kits.Assassin.Item", "GOLD_SWORD");
            var1.set("Kits.Assassin.Price", 600);
            var1.set("Kits.Assassin.Armor.Helmet", "LEATHER_HELMET : 1 : dye:RED");
            var1.set("Kits.Assassin.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Assassin.Armor.Leggings", "LEATHER_LEGGINGS : 1");
            var1.set("Kits.Assassin.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("GOLD_SWORD:32 : 1 : enchant:DAMAGE_ALL:22");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Assassin.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Assassin.Abilities", var3);
            var1.set("Kits.Assassin.Potion-Effects", Arrays.asList());
            var1.set("Kits.Assassin.Description", Arrays.asList("&bGet an&b extra almost insta kill sword", "&bthat only has 1 use per life"));
         }

         if (!var1.contains("Kits.Zombie")) {
            var1.set("Kits.Zombie.Enabled", true);
            var1.set("Kits.Zombie.Require-Permission", false);
            var1.set("Kits.Zombie.Item", "SKULL_ITEM:2");
            var1.set("Kits.Zombie.Price", 1000);
            var1.set("Kits.Zombie.Armor.Helmet", "LEATHER_HELMET : 1 : dye:GREEN");
            var1.set("Kits.Zombie.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Zombie.Armor.Leggings", "CHAINMAIL_LEGGINGS : 1");
            var1.set("Kits.Zombie.Armor.Boots", "LEATHER_BOOTS : 1 : dye:GREEN");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Zombie.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Zombie.Abilities", var3);
            var1.set("Kits.Zombie.Potion-Effects", Arrays.asList("HEALTH_BOOST : 999999 : 5"));
            var1.set("Kits.Zombie.Description", Arrays.asList("&bStart with extra bar of hearts"));
         }

         if (!var1.contains("Kits.Kangaroo")) {
            var1.set("Kits.Kangaroo.Enabled", true);
            var1.set("Kits.Kangaroo.Require-Permission", false);
            var1.set("Kits.Kangaroo.Item", "FIREWORK");
            var1.set("Kits.Kangaroo.Price", 800);
            var1.set("Kits.Kangaroo.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Kangaroo.Armor.Chestplate", "LEATHER_CHESTPLATE : 1 : dye:YELLOW");
            var1.set("Kits.Kangaroo.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Kangaroo.Armor.Boots", "DIAMOND_BOOTS : 1 : enchant:PROTECTION_FALL:5");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("FIREWORK : 1 : name:&cKangaroo rocket");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Kangaroo.Items", var2);
            var3 = new ArrayList();
            var3.add("Kangaroo");
            var1.set("Kits.Kangaroo.Abilities", var3);
            var1.set("Kits.Kangaroo.Potion-Effects", Arrays.asList());
            var1.set("Kits.Kangaroo.Description", Arrays.asList("&bGet a rocket that will launch you", "&bin the way you are looking!", "&bAnd its almost anti fall kit!", "&bTip: Shifting will boost you", "&bForward!"));
         }

         if (!var1.contains("Kits.Hades")) {
            var1.set("Kits.Hades.Enabled", true);
            var1.set("Kits.Hades.Require-Permission", false);
            var1.set("Kits.Hades.Item", "BONE");
            var1.set("Kits.Hades.Price", 1200);
            var1.set("Kits.Hades.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Hades.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Hades.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Hades.Armor.Boots", "LEATHER_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BONE : 1 : name:&cSummoning bone");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Hades.Items", var2);
            var3 = new ArrayList();
            var3.add("Hades");
            var1.set("Kits.Hades.Abilities", var3);
            var1.set("Kits.Hades.Potion-Effects", Arrays.asList());
            var1.set("Kits.Hades.Description", Arrays.asList("&bGet a bone that will summon", "&bDogs that will fight for you!"));
         }

         if (!var1.contains("Kits.Switcher")) {
            var1.set("Kits.Switcher.Enabled", true);
            var1.set("Kits.Switcher.Require-Permission", false);
            var1.set("Kits.Switcher.Item", "SNOW_BALL");
            var1.set("Kits.Switcher.Price", 600);
            var1.set("Kits.Switcher.Armor.Helmet", "LEATHER_HELMET : 1 : dye:WHITE");
            var1.set("Kits.Switcher.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Switcher.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Switcher.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("SNOW_BALL : 16 : name:&cSwitch!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Switcher.Items", var2);
            var3 = new ArrayList();
            var3.add("Switcher");
            var1.set("Kits.Switcher.Abilities", var3);
            var1.set("Kits.Switcher.Potion-Effects", Arrays.asList());
            var1.set("Kits.Switcher.Description", Arrays.asList("&bThrow a snowball at someone", "&bTo swap your locations!"));
         }

         if (!var1.contains("Kits.Thor")) {
            var1.set("Kits.Thor.Enabled", true);
            var1.set("Kits.Thor.Require-Permission", false);
            var1.set("Kits.Thor.Item", "WOOD_AXE");
            var1.set("Kits.Thor.Price", 900);
            var1.set("Kits.Thor.Armor.Helmet", "LEATHER_HELMET : 1");
            var1.set("Kits.Thor.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Thor.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Thor.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("WOOD_AXE : 1 : name:&cAlmighty axe!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Thor.Items", var2);
            var3 = new ArrayList();
            var3.add("Thor");
            var1.set("Kits.Thor.Abilities", var3);
            var1.set("Kits.Thor.Potion-Effects", Arrays.asList());
            var1.set("Kits.Thor.Description", Arrays.asList("&bGet an axe that will", "&bStrike lightning on nearby enemies!"));
         }

         if (!var1.contains("Kits.Stomper")) {
            var1.set("Kits.Stomper.Enabled", true);
            var1.set("Kits.Stomper.Require-Permission", false);
            var1.set("Kits.Stomper.Item", "FEATHER");
            var1.set("Kits.Stomper.Price", 500);
            var1.set("Kits.Stomper.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Stomper.Armor.Chestplate", "LEATHER_CHESTPLATE : 1 : dye:RED");
            var1.set("Kits.Stomper.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Stomper.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Stomper.Items", var2);
            var3 = new ArrayList();
            var3.add("Stomper");
            var1.set("Kits.Stomper.Abilities", var3);
            var1.set("Kits.Stomper.Potion-Effects", Arrays.asList());
            var1.set("Kits.Stomper.Description", Arrays.asList("&bJump on someone from a height", "&bTo deal some damage!", "&bThe higher you are the more", "&bDamage you will deal", "&bBut if the target is shifting", "&bYou wont deal as much damage!", "&bYou also have a MAX fall damage", "&bSo you wont die from falling"));
         }

         if (!var1.contains("Kits.Ghost")) {
            var1.set("Kits.Ghost.Enabled", true);
            var1.set("Kits.Ghost.Require-Permission", false);
            var1.set("Kits.Ghost.Item", "POTION:INVISIBILITY:false:false");
            var1.set("Kits.Ghost.Price", 400);
            var1.set("Kits.Ghost.Armor.Helmet", "");
            var1.set("Kits.Ghost.Armor.Chestplate", "");
            var1.set("Kits.Ghost.Armor.Leggings", "");
            var1.set("Kits.Ghost.Armor.Boots", "");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:1");
            var2.add("");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Ghost.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Ghost.Abilities", var3);
            var1.set("Kits.Ghost.Potion-Effects", Arrays.asList("INVISIBILITY : 999999 : 1"));
            var1.set("Kits.Ghost.Description", Arrays.asList("&bBe invisible with a potion effect", "&bUnfortunately, you wont have armor", "&bTo be fully invisible!"));
         }

         if (!var1.contains("Kits.Miner")) {
            var1.set("Kits.Miner.Enabled", true);
            var1.set("Kits.Miner.Require-Permission", false);
            var1.set("Kits.Miner.Item", "LEATHER_HELMET");
            var1.set("Kits.Miner.Price", 700);
            var1.set("Kits.Miner.Armor.Helmet", "LEATHER_HELMET : 1 : dye:YELLOW");
            var1.set("Kits.Miner.Armor.Chestplate", "LEATHER_CHESTPLATE : 1 : dye:YELLOW");
            var1.set("Kits.Miner.Armor.Leggings", "LEATHER_LEGGINGS : 1 : dye:YELLOW");
            var1.set("Kits.Miner.Armor.Boots", "LEATHER_BOOTS : 1 : dye:YELLOW");
            var2 = new ArrayList();
            var2.add("STONE_SWORD : 1 : enchant:DURABILITY:3");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Miner.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Miner.Abilities", var3);
            var1.set("Kits.Miner.Potion-Effects", Arrays.asList("INCREASE_DAMAGE : 999999 : 1", "SPEED : 999999 : 1", "NIGHT_VISION : 999999 : 1"));
            var1.set("Kits.Miner.Description", Arrays.asList("&bMining for hours has really taught", "&bYou to be strong and fast and can see", "&bIn darkness", "&bToo bad you actually never found any goods"));
         }

         if (!var1.contains("Kits.Spiderman")) {
            var1.set("Kits.Spiderman.Enabled", true);
            var1.set("Kits.Spiderman.Require-Permission", false);
            var1.set("Kits.Spiderman.Item", "WEB");
            var1.set("Kits.Spiderman.Price", 1000);
            var1.set("Kits.Spiderman.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Spiderman.Armor.Chestplate", "LEATHER_CHESTPLATE : 1 : dye:BLUE");
            var1.set("Kits.Spiderman.Armor.Leggings", "DIAMOND_LEGGINGS : 1");
            var1.set("Kits.Spiderman.Armor.Boots", "DIAMOND_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("WEB : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Spiderman.Items", var2);
            var3 = new ArrayList();
            var3.add("Spiderman");
            var1.set("Kits.Spiderman.Abilities", var3);
            var1.set("Kits.Spiderman.Potion-Effects", Arrays.asList());
            var1.set("Kits.Spiderman.Description", Arrays.asList("&bThrow snowballs at your target", "&bTo put them in a web for", "&bFew seconds!"));
         }

         if (!var1.contains("Kits.Prisoner")) {
            var1.set("Kits.Prisoner.Enabled", true);
            var1.set("Kits.Prisoner.Require-Permission", false);
            var1.set("Kits.Prisoner.Item", "LAVA_BUCKET");
            var1.set("Kits.Prisoner.Price", 2000);
            var1.set("Kits.Prisoner.Armor.Helmet", "DIAMOND_HELMET : 1");
            var1.set("Kits.Prisoner.Armor.Chestplate", "LEATHER_CHESTPLATE : 1 : dye:RED");
            var1.set("Kits.Prisoner.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Prisoner.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("DISPENSER : 1 : name:&4Prison");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Prisoner.Items", var2);
            var3 = new ArrayList();
            var3.add("Prisoner");
            var1.set("Kits.Prisoner.Abilities", var3);
            var1.set("Kits.Prisoner.Potion-Effects", Arrays.asList());
            var1.set("Kits.Prisoner.Description", Arrays.asList("&bShoot a snowball and send", "&bThose murderers to the LAVA jail"));
         }

         if (!var1.contains("Kits.Climber")) {
            var1.set("Kits.Climber.Enabled", true);
            var1.set("Kits.Climber.Require-Permission", false);
            var1.set("Kits.Climber.Item", "TRIPWIRE_HOOK");
            var1.set("Kits.Climber.Price", 350);
            var1.set("Kits.Climber.Armor.Helmet", "LEATHER_HELMET : 1 : dye:YELLOW");
            var1.set("Kits.Climber.Armor.Chestplate", "CHAINMAIL_CHESTPLATE : 1");
            var1.set("Kits.Climber.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Climber.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BOW : 1 : enchant:ARROW_INFINITE:1");
            var2.add("ARROW : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Climber.Items", var2);
            var3 = new ArrayList();
            var3.add("Climber");
            var1.set("Kits.Climber.Abilities", var3);
            var1.set("Kits.Climber.Potion-Effects", Arrays.asList());
            var1.set("Kits.Climber.Description", Arrays.asList("&bUse your bow to summon", "&bA magical chicken that will carry you"));
         }

         if (!var1.contains("Kits.Dragon")) {
            var1.set("Kits.Dragon.Enabled", true);
            var1.set("Kits.Dragon.Require-Permission", false);
            var1.set("Kits.Dragon.Item", "FIREBALL");
            var1.set("Kits.Dragon.Price", 1000);
            var1.set("Kits.Dragon.Armor.Helmet", "LEATHER_HELMET : 1 : dye:RED");
            var1.set("Kits.Dragon.Armor.Chestplate", "DIAMOND_CHESTPLATE : 1");
            var1.set("Kits.Dragon.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Dragon.Armor.Boots", "LEATHER_BOOTS : 1 : dye:RED");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("FIREBALL : 1 : name:&cFlames");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Dragon.Items", var2);
            var3 = new ArrayList();
            var3.add("Dragon");
            var1.set("Kits.Dragon.Abilities", var3);
            var1.set("Kits.Dragon.Potion-Effects", Arrays.asList("FIRE_RESISTANCE : 999999 : 1"));
            var1.set("Kits.Dragon.Description", Arrays.asList("&bBreathe fire!", "&bAnd be immune for fire damage"));
         }

         if (!var1.contains("Kits.Suicidal")) {
            var1.set("Kits.Suicidal.Enabled", true);
            var1.set("Kits.Suicidal.Require-Permission", false);
            var1.set("Kits.Suicidal.Item", "TNT");
            var1.set("Kits.Suicidal.Price", 250);
            var1.set("Kits.Suicidal.Armor.Helmet", "LEATHER_HELMET : 1 : dye:RED");
            var1.set("Kits.Suicidal.Armor.Chestplate", "LEATHER_CHESTPLATE : 1 : dye:RED");
            var1.set("Kits.Suicidal.Armor.Leggings", "LEATHER_LEGGINGS : 1 : dye:RED");
            var1.set("Kits.Suicidal.Armor.Boots", "LEATHER_BOOTS : 1 : dye:RED");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("REDSTONE_TORCH_ON : 1 : name:&cSuicide!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Suicidal.Items", var2);
            var3 = new ArrayList();
            var3.add("Suicidal");
            var1.set("Kits.Suicidal.Abilities", var3);
            var1.set("Kits.Suicidal.Potion-Effects", Arrays.asList("SPEED : 999999 : 1"));
            var1.set("Kits.Suicidal.Description", Arrays.asList("&bUse your torch to active", "&bThe tnt inside you and", "&bBlow up along nearby enemies"));
         }

         if (!var1.contains("Kits.Phantom")) {
            var1.set("Kits.Phantom.Enabled", true);
            var1.set("Kits.Phantom.Require-Permission", false);
            var1.set("Kits.Phantom.Item", "BOOK");
            var1.set("Kits.Phantom.Price", 800);
            var1.set("Kits.Phantom.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Phantom.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Phantom.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Phantom.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BOOK : 1 : name:&cFly!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Phantom.Items", var2);
            var3 = new ArrayList();
            var3.add("Phantom");
            var1.set("Kits.Phantom.Abilities", var3);
            var1.set("Kits.Phantom.Potion-Effects", Arrays.asList());
            var1.set("Kits.Phantom.Description", Arrays.asList("&bUse your magical book to have the", "&bAbility of flying for few seconds"));
         }

         if (!var1.contains("Kits.Timelord")) {
            var1.set("Kits.Timelord.Enabled", true);
            var1.set("Kits.Timelord.Require-Permission", false);
            var1.set("Kits.Timelord.Item", "WATCH");
            var1.set("Kits.Timelord.Price", 900);
            var1.set("Kits.Timelord.Armor.Helmet", "LEATHER_HELMET : 1 : dye:BLACK");
            var1.set("Kits.Timelord.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Timelord.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Timelord.Armor.Boots", "LEATHER_BOOTS : 1 : dye:BLACK");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("WATCH : 1 : name:&cFreeze!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Timelord.Items", var2);
            var3 = new ArrayList();
            var3.add("Timelord");
            var1.set("Kits.Timelord.Abilities", var3);
            var1.set("Kits.Timelord.Potion-Effects", Arrays.asList());
            var1.set("Kits.Timelord.Description", Arrays.asList("&bUse your magical clock to", "&bPrevent players around you", "&bFrom moving"));
         }

         if (!var1.contains("Kits.Fisherman")) {
            var1.set("Kits.Fisherman.Enabled", true);
            var1.set("Kits.Fisherman.Require-Permission", false);
            var1.set("Kits.Fisherman.Item", "FISHING_ROD");
            var1.set("Kits.Fisherman.Price", 900);
            var1.set("Kits.Fisherman.Armor.Helmet", "LEATHER_HELMET : 1");
            var1.set("Kits.Fisherman.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Fisherman.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Fisherman.Armor.Boots", "LEATHER_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:1");
            var2.add("FISHING_ROD : 1 : enchant:DURABILITY:2");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Fisherman.Items", var2);
            var3 = new ArrayList();
            var3.add("Fisherman");
            var1.set("Kits.Fisherman.Abilities", var3);
            var1.set("Kits.Fisherman.Potion-Effects", Arrays.asList());
            var1.set("Kits.Fisherman.Description", Arrays.asList("&bGet a fishing rod that", "&bAllows you to teleport caught", "&bEntities to you"));
         }

         if (!var1.contains("Kits.Vanilla")) {
            var1.set("Kits.Vanilla.Enabled", true);
            var1.set("Kits.Vanilla.Require-Permission", false);
            var1.set("Kits.Vanilla.Item", "SPLASH_POTION:INSTANT_HEAL:false:true");
            var1.set("Kits.Vanilla.Price", 200);
            var1.set("Kits.Vanilla.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Vanilla.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Vanilla.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Vanilla.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:1");
            var2.add("SPLASH_POTION:REGEN:false:false : 1");
            var2.add("SPLASH_POTION:SPEED:true:false : 1");
            var2.add("SPLASH_POTION:POISON:false:false : 1");
            var2.add("SPLASH_POTION:SLOWNESS:false:false : 1");
            var2.add("SPLASH_POTION:INSTANT_DAMAGE:false:false : 1");
            var2.add("SPLASH_POTION:INSTANT_DAMAGE:false:false : 1");

            for(int var4 = 0; var4 < 29; ++var4) {
               var2.add("SPLASH_POTION:INSTANT_HEAL:false:true : 1");
            }

            var1.set("Kits.Vanilla.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Vanilla.Abilities", var3);
            var1.set("Kits.Vanilla.Potion-Effects", Arrays.asList());
            var1.set("Kits.Vanilla.Description", Arrays.asList("&bGet some really good", "&bSplash potions! but instead of soup", "&bYou have instant heal 2 splash potions"));
         }

         if (!var1.contains("Kits.Burrower")) {
            var1.set("Kits.Burrower.Enabled", true);
            var1.set("Kits.Burrower.Require-Permission", false);
            var1.set("Kits.Burrower.Item", "BRICK");
            var1.set("Kits.Burrower.Price", 450);
            var1.set("Kits.Burrower.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Burrower.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Burrower.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Burrower.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BRICK : 1 : name:&cPanic room");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Burrower.Items", var2);
            var3 = new ArrayList();
            var3.add("Burrower");
            var1.set("Kits.Burrower.Abilities", var3);
            var1.set("Kits.Burrower.Potion-Effects", Arrays.asList());
            var1.set("Kits.Burrower.Description", Arrays.asList("&bGet a brick that will", "&bBuild a panic room for you", "&bYou will be safe in there"));
         }

         if (!var1.contains("Kits.Zen")) {
            var1.set("Kits.Zen.Enabled", true);
            var1.set("Kits.Zen.Require-Permission", false);
            var1.set("Kits.Zen.Item", "SLIME_BALL");
            var1.set("Kits.Zen.Price", 500);
            var1.set("Kits.Zen.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Zen.Armor.Chestplate", "CHAINMAIL_CHESTPLATE : 1");
            var1.set("Kits.Zen.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Zen.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("SLIME_BALL : 1 : name:&cTeleporter");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Zen.Items", var2);
            var3 = new ArrayList();
            var3.add("Zen");
            var1.set("Kits.Zen.Abilities", var3);
            var1.set("Kits.Zen.Potion-Effects", Arrays.asList());
            var1.set("Kits.Zen.Description", Arrays.asList("&bGet a slime ball that will", "&bTeleport you to the nearest", "&bEnemy!"));
         }

         if (!var1.contains("Kits.Viking")) {
            var1.set("Kits.Viking.Enabled", true);
            var1.set("Kits.Viking.Require-Permission", false);
            var1.set("Kits.Viking.Item", "DIAMOND_AXE");
            var1.set("Kits.Viking.Price", 300);
            var1.set("Kits.Viking.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Viking.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Viking.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Viking.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_AXE : 1 : enchant:DAMAGE_ALL:1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Viking.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Viking.Abilities", var3);
            var1.set("Kits.Viking.Potion-Effects", Arrays.asList());
            var1.set("Kits.Viking.Description", Arrays.asList("&bA basic kit with a diamond axe", "&bInstead of a diamond sword"));
         }

         if (!var1.contains("Kits.Viper")) {
            var1.set("Kits.Viper.Enabled", true);
            var1.set("Kits.Viper.Require-Permission", false);
            var1.set("Kits.Viper.Item", "EYE_OF_ENDER");
            var1.set("Kits.Viper.Price", 600);
            var1.set("Kits.Viper.Armor.Helmet", "LEATHER_HELMET : 1 : dye:GREEN");
            var1.set("Kits.Viper.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Viper.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Viper.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Viper.Items", var2);
            var3 = new ArrayList();
            var3.add("Viper");
            var1.set("Kits.Viper.Abilities", var3);
            var1.set("Kits.Viper.Potion-Effects", Arrays.asList());
            var1.set("Kits.Viper.Description", Arrays.asList("&bHave a chance of biting", "&bYour enemy while fighting", "&bWhich gives them a poison effect"));
         }

         if (!var1.contains("Kits.Monk")) {
            var1.set("Kits.Monk.Enabled", true);
            var1.set("Kits.Monk.Require-Permission", false);
            var1.set("Kits.Monk.Item", "BLAZE_ROD");
            var1.set("Kits.Monk.Price", 700);
            var1.set("Kits.Monk.Armor.Helmet", "LEATHER_HELMET : 1 : dye:YELLOW");
            var1.set("Kits.Monk.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Monk.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Monk.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BLAZE_ROD : 1 : name:&cRight click a player to use!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Monk.Items", var2);
            var3 = new ArrayList();
            var3.add("Monk");
            var1.set("Kits.Monk.Abilities", var3);
            var1.set("Kits.Monk.Potion-Effects", Arrays.asList());
            var1.set("Kits.Monk.Description", Arrays.asList("&bGet a magical rod that will", "&bSwap the item in the target hand", "&bWith a random item in their hotbar!"));
         }

         if (!var1.contains("Kits.Dracula")) {
            var1.set("Kits.Dracula.Enabled", true);
            var1.set("Kits.Dracula.Require-Permission", false);
            var1.set("Kits.Dracula.Item", "REDSTONE");
            var1.set("Kits.Dracula.Price", 1300);
            var1.set("Kits.Dracula.Armor.Helmet", "LEATHER_HELMET : 1 : dye:RED");
            var1.set("Kits.Dracula.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Dracula.Armor.Leggings", "LEATHER_LEGGINGS : 1 : dye:RED");
            var1.set("Kits.Dracula.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:1 : name:&4Blood sucker");
            var2.add("REDSTONE : 1 : name:&cRight click to activate");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Dracula.Items", var2);
            var3 = new ArrayList();
            var3.add("Dracula");
            var1.set("Kits.Dracula.Abilities", var3);
            var1.set("Kits.Dracula.Potion-Effects", Arrays.asList());
            var1.set("Kits.Dracula.Description", Arrays.asList("&bGet a sword that sucks the blood", "&bOf your enemies and have the ability", "&bTo suck enemies potion effects", "&bBut your blood can only handle", "&bThese effects for few seconds!"));
         }

         if (!var1.contains("Kits.Hulk")) {
            var1.set("Kits.Hulk.Enabled", true);
            var1.set("Kits.Hulk.Require-Permission", false);
            var1.set("Kits.Hulk.Item", "PISTON_STICKY_BASE");
            var1.set("Kits.Hulk.Price", 900);
            var1.set("Kits.Hulk.Armor.Helmet", "LEATHER_HELMET : 1 : dye:GREEN");
            var1.set("Kits.Hulk.Armor.Chestplate", "DIAMOND_CHESTPLATE : 1");
            var1.set("Kits.Hulk.Armor.Leggings", "CHAINMAIL_LEGGINGS : 1");
            var1.set("Kits.Hulk.Armor.Boots", "LEATHER_BOOTS : 1 : dye:GREEN");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("PISTON_STICKY_BASE : 1 : name:&cSmash!");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Hulk.Items", var2);
            var3 = new ArrayList();
            var3.add("Hulk");
            var1.set("Kits.Hulk.Abilities", var3);
            var1.set("Kits.Hulk.Potion-Effects", Arrays.asList());
            var1.set("Kits.Hulk.Description", Arrays.asList("&bSmash the ground and release your", "&bAnger! Causing blocks and players", "&bTo fly away"));
         }

         if (!var1.contains("Kits.Cactus")) {
            var1.set("Kits.Cactus.Enabled", true);
            var1.set("Kits.Cactus.Require-Permission", false);
            var1.set("Kits.Cactus.Item", "CACTUS");
            var1.set("Kits.Cactus.Price", 700);
            var1.set("Kits.Cactus.Armor.Helmet", "IRON_HELMET : 1 : enchant:THORNS:2");
            var1.set("Kits.Cactus.Armor.Chestplate", "IRON_CHESTPLATE : 1 : enchant:THORNS:2");
            var1.set("Kits.Cactus.Armor.Leggings", "LEATHER_LEGGINGS : 1 : enchant:THORNS:2");
            var1.set("Kits.Cactus.Armor.Boots", "LEATHER_BOOTS : 1 : enchant:THORNS:2");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Cactus.Items", var2);
            var3 = new ArrayList();
            var1.set("Kits.Cactus.Abilities", var3);
            var1.set("Kits.Cactus.Potion-Effects", Arrays.asList());
            var1.set("Kits.Cactus.Description", Arrays.asList("&bGet thorns armor!"));
         }

         if (!var1.contains("Kits.Rider")) {
            var1.set("Kits.Rider.Enabled", true);
            var1.set("Kits.Rider.Require-Permission", false);
            var1.set("Kits.Rider.Item", "DIAMOND_BARDING");
            var1.set("Kits.Rider.Price", 400);
            var1.set("Kits.Rider.Armor.Helmet", "LEATHER_HELMET : 1");
            var1.set("Kits.Rider.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Rider.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Rider.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("DIAMOND_BARDING : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Rider.Items", var2);
            var3 = new ArrayList();
            var3.add("Rider");
            var1.set("Kits.Rider.Abilities", var3);
            var1.set("Kits.Rider.Potion-Effects", Arrays.asList());
            var1.set("Kits.Rider.Description", Arrays.asList("&bSummon a horse that you can ride!"));
         }

         if (!var1.contains("Kits.Summoner")) {
            var1.set("Kits.Summoner.Enabled", true);
            var1.set("Kits.Summoner.Require-Permission", false);
            var1.set("Kits.Summoner.Item", "IRON_BLOCK");
            var1.set("Kits.Summoner.Price", 600);
            var1.set("Kits.Summoner.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Summoner.Armor.Chestplate", "CHAINMAIL_CHESTPLATE : 1");
            var1.set("Kits.Summoner.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Summoner.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("IRON_BLOCK : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Summoner.Items", var2);
            var3 = new ArrayList();
            var3.add("Summoner");
            var1.set("Kits.Summoner.Abilities", var3);
            var1.set("Kits.Summoner.Potion-Effects", Arrays.asList());
            var1.set("Kits.Summoner.Description", Arrays.asList("&bSummon an iron golem that will", "&bAttack any one attacks him"));
         }

         if (!var1.contains("Kits.Souper")) {
            var1.set("Kits.Souper.Enabled", true);
            var1.set("Kits.Souper.Require-Permission", false);
            var1.set("Kits.Souper.Item", "CHEST");
            var1.set("Kits.Souper.Price", 500);
            var1.set("Kits.Souper.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Souper.Armor.Chestplate", "CHAINMAIL_CHESTPLATE : 1");
            var1.set("Kits.Souper.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Souper.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("CHEST : 1");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Souper.Items", var2);
            var3 = new ArrayList();
            var3.add("Souper");
            var1.set("Kits.Souper.Abilities", var3);
            var1.set("Kits.Souper.Potion-Effects", Arrays.asList());
            var1.set("Kits.Souper.Description", Arrays.asList("&bAutomatically refill your hotbar with available soup!"));
         }

         if (!var1.contains("Kits.Bane")) {
            var1.set("Kits.Bane.Enabled", true);
            var1.set("Kits.Bane.Require-Permission", false);
            var1.set("Kits.Bane.Item", "GOLD_HOE");
            var1.set("Kits.Bane.Price", 500);
            var1.set("Kits.Bane.Armor.Helmet", "GOLD_HELMET : 1");
            var1.set("Kits.Bane.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Bane.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Bane.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("GOLD_HOE : 1 : name:&5Curse");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Bane.Items", var2);
            var3 = new ArrayList();
            var3.add("Bane");
            var1.set("Kits.Bane.Abilities", var3);
            var1.set("Kits.Bane.Potion-Effects", Arrays.asList());
            var1.set("Kits.Bane.Description", Arrays.asList("&bCurse someone to give them blindness,", "&bslowness, and weakness!", "&bbut you are unable to damage them", "&bmuch during this curse"));
         }

         if (!var1.contains("Kits.Sunder")) {
            var1.set("Kits.Sunder.Enabled", true);
            var1.set("Kits.Sunder.Require-Permission", false);
            var1.set("Kits.Sunder.Item", "BEACON");
            var1.set("Kits.Sunder.Price", 500);
            var1.set("Kits.Sunder.Armor.Helmet", "CHAINMAIL_HELMET : 1");
            var1.set("Kits.Sunder.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Sunder.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Sunder.Armor.Boots", "CHAINMAIL_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BEACON : 1 : name:&4Swap");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Sunder.Items", var2);
            var3 = new ArrayList();
            var3.add("Sunder");
            var1.set("Kits.Sunder.Abilities", var3);
            var1.set("Kits.Sunder.Potion-Effects", Arrays.asList());
            var1.set("Kits.Sunder.Description", Arrays.asList("&bSwap your health with the targets health!"));
         }

         if (!var1.contains("Kits.Centaur")) {
            var1.set("Kits.Centaur.Enabled", true);
            var1.set("Kits.Centaur.Require-Permission", false);
            var1.set("Kits.Centaur.Item", "SULPHUR");
            var1.set("Kits.Centaur.Price", 500);
            var1.set("Kits.Centaur.Armor.Helmet", "LEATHER_HELMET : 1 : dye:RED");
            var1.set("Kits.Centaur.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Centaur.Armor.Leggings", "DIAMOND_LEGGINGS : 1");
            var1.set("Kits.Centaur.Armor.Boots", "CHAINMAIL_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("SULPHUR : 1 : name:&cStrike");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Centaur.Items", var2);
            var3 = new ArrayList();
            var3.add("Centaur");
            var1.set("Kits.Centaur.Abilities", var3);
            var1.set("Kits.Centaur.Potion-Effects", Arrays.asList());
            var1.set("Kits.Centaur.Description", Arrays.asList("&bDeal massive area damage that", "&beven damages you, if the targets", "&bsurvive though they get a regen effect"));
         }

         if (!var1.contains("Kits.Blinker")) {
            var1.set("Kits.Blinker.Enabled", true);
            var1.set("Kits.Blinker.Require-Permission", false);
            var1.set("Kits.Blinker.Item", "GHAST_TEAR");
            var1.set("Kits.Blinker.Price", 500);
            var1.set("Kits.Blinker.Armor.Helmet", "LEATHER_HELMET : 1 : dye:YELLOW : enchant:PROTECTION_EXPLOSIONS:1");
            var1.set("Kits.Blinker.Armor.Chestplate", "IRON_CHESTPLATE : 1 : enchant:PROTECTION_EXPLOSIONS:1");
            var1.set("Kits.Blinker.Armor.Leggings", "IRON_LEGGINGS : 1 : enchant:PROTECTION_EXPLOSIONS:1");
            var1.set("Kits.Blinker.Armor.Boots", "DIAMOND_BOOTS : 1 : enchant:PROTECTION_EXPLOSIONS:1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1");
            var2.add("BOW : 1 : enchant:ARROW_INFINITE:1 : name:&6Blink");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var2.add("ARROW : 1");
            var1.set("Kits.Blinker.Items", var2);
            var3 = new ArrayList();
            var3.add("Blinker");
            var1.set("Kits.Blinker.Abilities", var3);
            var1.set("Kits.Blinker.Potion-Effects", Arrays.asList());
            var1.set("Kits.Blinker.Description", Arrays.asList("&bTeleport to targets that have been", "&bhit with your arrows and release a tnt", "&bupon them! The arrows themselves deal", "&ba very low damage though!"));
         }
      }

      if (this.plugin.getConfig().getBoolean("Create-Default-Upgrades")) {
         this.plugin.getConfig().set("Create-Default-Upgrades", false);
         this.plugin.saveConfig();
         if (!var1.contains("Kits.Pvp+")) {
            var1.set("Kits.Pvp+.Enabled", true);
            var1.set("Kits.Pvp+.Require-Permission", false);
            var1.set("Kits.Pvp+.Item", "DIAMOND_SWORD");
            var1.set("Kits.Pvp+.Price", 750);
            var1.set("Kits.Pvp+.Armor.Helmet", "IRON_HELMET : 1");
            var1.set("Kits.Pvp+.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Pvp+.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Pvp+.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:2");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Pvp+.Items", var2);
            var1.set("Kits.Pvp+.Abilities", new ArrayList());
            var1.set("Kits.Pvp+.Potion-Effects", Arrays.asList());
            var1.set("Kits.Pvp+.Description", Arrays.asList("&bA very basic but strong kit!", "&a+ Increased sword sharpness"));
            var1.set("Kits.Pvp+.Upgraded-Version-Of", "Pvp");
         }

         if (!var1.contains("Kits.Pvp++")) {
            var1.set("Kits.Pvp++.Enabled", true);
            var1.set("Kits.Pvp++.Require-Permission", false);
            var1.set("Kits.Pvp++.Item", "DIAMOND_SWORD");
            var1.set("Kits.Pvp++.Price", 1000);
            var1.set("Kits.Pvp++.Armor.Helmet", "IRON_HELMET : 1 : enchant:PROTECTION_ENVIRONMENTAL:2");
            var1.set("Kits.Pvp++.Armor.Chestplate", "IRON_CHESTPLATE : 1");
            var1.set("Kits.Pvp++.Armor.Leggings", "IRON_LEGGINGS : 1");
            var1.set("Kits.Pvp++.Armor.Boots", "IRON_BOOTS : 1");
            var2 = new ArrayList();
            var2.add("DIAMOND_SWORD : 1 : enchant:DAMAGE_ALL:2");
            var2.add("AUTO_FILL_HEAL_ITEM");
            var1.set("Kits.Pvp++.Items", var2);
            var1.set("Kits.Pvp++.Abilities", new ArrayList());
            var1.set("Kits.Pvp++.Potion-Effects", Arrays.asList());
            var1.set("Kits.Pvp++.Description", Arrays.asList("&bA very basic but strong kit!", "&a+ Increased sword sharpness", "&a+ Increased armor protection"));
            var1.set("Kits.Pvp++.Upgraded-Version-Of", "Pvp+");
         }
      }

      this.saveConfig("kits.yml");
   }

   public void loadKits() {
      FileConfiguration var1 = this.getConfig("kits.yml");
      if (var1.getConfigurationSection("Kits") != null) {
         ArrayList var2 = new ArrayList();
         Iterator var4 = var1.getConfigurationSection("Kits").getKeys(false).iterator();

         String var3;
         while(var4.hasNext()) {
            var3 = (String)var4.next();
            ItemStackBuilder var5 = null;
            ItemStack[] var6 = new ItemStack[4];
            ItemStack[] var7 = new ItemStack[36];
            boolean var8 = var1.getBoolean("Kits." + var3 + ".Enabled");
            boolean var9 = var1.getBoolean("Kits." + var3 + ".Require-Permission");

            String var11;
            String var12;
            Iterator var13;
            try {
               String var10 = var1.getString("Kits." + var3 + ".Item");
               var11 = var10.contains(":") ? var10.split(":")[0] : var10;
               var5 = new ItemStackBuilder(Material.getMaterial(var11));
               if (var10.contains(":")) {
                  if ((var11.equalsIgnoreCase("POTION") || var11.equalsIgnoreCase("SPLASH_POTION") || var11.equalsIgnoreCase("LINGERING_POTION")) && var10.split(":").length == 4) {
                     var5.setPotionEffect(PotionType.valueOf(var10.split(":")[1]), Boolean.valueOf(var10.split(":")[2]), Boolean.valueOf(var10.split(":")[3]));
                  } else {
                     var5.setDurability(Integer.valueOf(var10.split(":")[1]));
                  }
               }

               var5.setName(ChatColor.GREEN + var3);
               var13 = var1.getStringList("Kits." + var3 + ".Description").iterator();

               while(var13.hasNext()) {
                  var12 = (String)var13.next();
                  var5.addLore(ChatColor.translateAlternateColorCodes('&', var12));
               }
            } catch (Exception var23) {
               this.plugin.logger.info("[KitBattle] Failed to create the logo for the kit: " + var3 + ", due to that, the whole kit wont load!, make sure you have the correct format!");
               var23.printStackTrace();
               continue;
            }

            int var24 = 0;
            Iterator var26 = this.armorParts.iterator();

            while(var26.hasNext()) {
               var11 = (String)var26.next();

               try {
                  var6[var24] = var1.getString("Kits." + var3 + ".Armor." + var11).isEmpty() ? new ItemStack(Material.AIR) : this.getItemstackFilled(var1.getString("Kits." + var3 + ".Armor." + var11));
                  ++var24;
               } catch (Exception var21) {
                  this.plugin.logger.info("[KitBattle] Failed to create a " + var11 + " for the kit: " + var3 + ", due to that, the whole kit wont load!, make sure you have the correct format!");
                  var21.printStackTrace();
               }
            }

            int var25 = 0;
            var13 = var1.getStringList("Kits." + var3 + ".Items").iterator();

            label127:
            while(var13.hasNext()) {
               var12 = (String)var13.next();

               try {
                  if (var12.equalsIgnoreCase("AUTO_FILL_HEAL_ITEM")) {
                     int var14 = 0;

                     while(true) {
                        if (var14 >= var7.length) {
                           break label127;
                        }

                        if (var7[var14] == null) {
                           var7[var14] = this.plugin.listen.soup;
                        }

                        ++var14;
                     }
                  }

                  if (var25 == var7.length) {
                     break;
                  }

                  var7[var25++] = var12.isEmpty() ? new ItemStack(Material.AIR) : this.getItemstackFilled(var12);
               } catch (Exception var22) {
                  this.plugin.logger.info("[KitBattle] Failed to create this item: " + var12 + " for the kit: " + var3 + ", due to that, the whole kit wont load!, make sure you have the correct format!");
                  var22.printStackTrace();
               }
            }

            int var27 = var1.getInt("Kits." + var3 + ".Price");
            ArrayList var28 = new ArrayList();
            Iterator var15 = var1.getStringList("Kits." + var3 + ".Potion-Effects").iterator();

            while(var15.hasNext()) {
               String var29 = (String)var15.next();
               String[] var16 = var29.split(" : ");
               var28.add(new PotionEffect(PotionEffectType.getByName(var16[0]), Integer.valueOf(var16[1]) * 20, Integer.valueOf(var16[2]) - 1));
            }

            ArrayList var30 = new ArrayList();
            Iterator var32 = var1.getStringList("Kits." + var3 + ".Abilities").iterator();

            while(var32.hasNext()) {
               String var31 = (String)var32.next();
               Ability[] var20;
               int var19 = (var20 = Ability.values()).length;

               for(int var18 = 0; var18 < var19; ++var18) {
                  Ability var17 = var20[var18];
                  if (var31.equalsIgnoreCase(var17.name())) {
                     var30.add(var17);
                  }
               }
            }

            if (var1.contains("Kits." + var3 + ".Upgraded-Version-Of")) {
               var2.add(var3);
            }

            this.plugin.Kits.put(var3.toLowerCase(), new Kit(this.plugin, var3, var5.build(), var7, var6, var27, var28, var30, var8, var9));
         }

         var4 = var2.iterator();

         while(var4.hasNext()) {
            var3 = (String)var4.next();
            if (this.plugin.Kits.containsKey(var1.getString("Kits." + var3 + ".Upgraded-Version-Of").toLowerCase())) {
               ((Kit)this.plugin.Kits.get(var3.toLowerCase())).original = (Kit)this.plugin.Kits.get(var1.getString("Kits." + var3 + ".Upgraded-Version-Of").toLowerCase());
            }
         }

      }
   }

   public ItemStack getItemstackFilled(String var1) {
      String[] var2 = var1.split(" : ");
      String var3 = var2[0].contains(":") ? var2[0].split(":")[0] : var2[0];
      ItemStackBuilder var4 = new ItemStackBuilder(Material.getMaterial(var3));
      if (var2[0].contains(":")) {
         if ((var3.equalsIgnoreCase("POTION") || var3.equalsIgnoreCase("SPLASH_POTION") || var3.equalsIgnoreCase("LINGERING_POTION")) && var2[0].split(":").length == 4) {
            var4.setPotionEffect(PotionType.valueOf(var2[0].split(":")[1]), Boolean.valueOf(var2[0].split(":")[2]), Boolean.valueOf(var2[0].split(":")[3]));
         } else {
            var4.setDurability(Integer.valueOf(var2[0].split(":")[1]));
         }
      }

      var4.setAmount(Integer.valueOf(var2[1]));

      for(int var5 = 2; var5 < var2.length; ++var5) {
         if (var2[var5].split(":")[0].equalsIgnoreCase("enchant")) {
            var4.addEnchantment(Enchantment.getByName(var2[var5].split(":")[1]), Integer.valueOf(var2[var5].split(":")[2]));
         } else if (var2[var5].split(":")[0].equalsIgnoreCase("name")) {
            var4.setName(ChatColor.translateAlternateColorCodes('&', var2[var5].split(":")[1]));
         } else if (var2[var5].split(":")[0].equalsIgnoreCase("lore")) {
            var4.addLore(ChatColor.translateAlternateColorCodes('&', var2[var5].split(":")[1]));
         } else if (var2[var5].split(":")[0].equalsIgnoreCase("dye")) {
            var4.setColor(this.getColor(var2[var5].split(":")[1]));
         }
      }

      return var4.build();
   }

   public void setupRanks() {
      FileConfiguration var1 = this.getConfig("ranks.yml");
      if (var1.getConfigurationSection("Ranks") == null) {
         var1.set("Ranks.Newbie.Required-Exp", 0);
         var1.set("Ranks.Newbie.Commands-Excuted-When-Rank-Reached", Arrays.asList());
         var1.set("Ranks.Starter.Required-Exp", 25);
         var1.set("Ranks.Starter.Commands-Excuted-When-Rank-Reached", Arrays.asList("kb coins add %player% 500", "kb kitunlocker give %player% 1"));
         var1.set("Ranks.Survivor.Required-Exp", 75);
         var1.set("Ranks.Survivor.Commands-Excuted-When-Rank-Reached", Arrays.asList("kb coins add %player% 750", "kb kitunlocker give %player% 1"));
         var1.set("Ranks.Pro.Required-Exp", 150);
         var1.set("Ranks.Pro.Commands-Excuted-When-Rank-Reached", Arrays.asList("kb coins add %player% 1000", "kb kitunlocker give %player% 1"));
         var1.set("Ranks.Legend.Required-Exp", 300);
         var1.set("Ranks.Legend.Commands-Excuted-When-Rank-Reached", Arrays.asList("kb coins add %player% 2000", "kb kitunlocker give %player% 1"));
         var1.set("Ranks.Immortal.Required-Exp", 600);
         var1.set("Ranks.Immortal.Commands-Excuted-When-Rank-Reached", Arrays.asList("kb coins add %player% 4000", "kb kitunlocker give %player% 1"));
         var1.set("Ranks.God.Prefix", "&5[&ka&4God&5&ka&5] &c");
         var1.set("Ranks.God.Required-Exp", 1200);
         var1.set("Ranks.God.Commands-Excuted-When-Rank-Reached", Arrays.asList("kb coins add %player% 5000", "kb kitunlocker give %player% 1"));
      }

      this.saveConfig("ranks.yml");
   }

   public void loadRanks() {
      FileConfiguration var1 = this.getConfig("ranks.yml");
      if (var1.getConfigurationSection("Ranks") != null) {
         Iterator var3 = var1.getConfigurationSection("Ranks").getKeys(false).iterator();

         while(var3.hasNext()) {
            String var2 = (String)var3.next();
            int var4 = var1.getInt("Ranks." + var2 + ".Required-Exp");
            List var5 = var1.getStringList("Ranks." + var2 + ".Commands-Excuted-When-Rank-Reached");
            String var6 = ChatColor.translateAlternateColorCodes('&', var1.contains("Ranks." + var2 + ".Prefix") ? var1.getString("Ranks." + var2 + ".Prefix") : var1.getString("General-Ranks-Prefix"));
            this.plugin.Ranks.put(var2.toLowerCase(), new Rank(var2, var6, var4, var5));
         }

      }
   }

   public Color getColor(String var1) {
      Color var2 = Color.BLACK;
      if (var1.equalsIgnoreCase("BLACK")) {
         return var2;
      } else {
         if (var1.equalsIgnoreCase("AQUA")) {
            var2 = Color.AQUA;
         } else if (var1.equalsIgnoreCase("BLUE")) {
            var2 = Color.BLUE;
         } else if (var1.equalsIgnoreCase("FUCHSIA")) {
            var2 = Color.FUCHSIA;
         } else if (var1.equalsIgnoreCase("GRAY")) {
            var2 = Color.GRAY;
         } else if (var1.equalsIgnoreCase("GREEN")) {
            var2 = Color.GREEN;
         } else if (var1.equalsIgnoreCase("LIME")) {
            var2 = Color.LIME;
         } else if (var1.equalsIgnoreCase("MAROON")) {
            var2 = Color.MAROON;
         } else if (var1.equalsIgnoreCase("NAVY")) {
            var2 = Color.NAVY;
         } else if (var1.equalsIgnoreCase("OLIVE")) {
            var2 = Color.OLIVE;
         } else if (var1.equalsIgnoreCase("ORANGE")) {
            var2 = Color.ORANGE;
         } else if (var1.equalsIgnoreCase("PURPLE")) {
            var2 = Color.PURPLE;
         } else if (var1.equalsIgnoreCase("RED")) {
            var2 = Color.RED;
         } else if (var1.equalsIgnoreCase("SILVER")) {
            var2 = Color.SILVER;
         } else if (var1.equalsIgnoreCase("TEAL")) {
            var2 = Color.TEAL;
         } else if (var1.equalsIgnoreCase("WHITE")) {
            var2 = Color.WHITE;
         } else if (var1.equalsIgnoreCase("YELLOW")) {
            var2 = Color.YELLOW;
         }

         return var2;
      }
   }

   public String getColorName(Color var1) {
      String var2 = "BLACK";
      if (var1.equals(Color.BLACK)) {
         return var2;
      } else {
         if (var1.equals(Color.AQUA)) {
            var2 = "AQUA";
         } else if (var1.equals(Color.BLUE)) {
            var2 = "BLUE";
         } else if (var1.equals(Color.FUCHSIA)) {
            var2 = "FUCHSIA";
         } else if (var1.equals(Color.GRAY)) {
            var2 = "GRAY";
         } else if (var1.equals(Color.GREEN)) {
            var2 = "GREEN";
         } else if (var1.equals(Color.LIME)) {
            var2 = "LIME";
         } else if (var1.equals(Color.MAROON)) {
            var2 = "MAROON";
         } else if (var1.equals(Color.NAVY)) {
            var2 = "NAVY";
         } else if (var1.equals(Color.OLIVE)) {
            var2 = "OLIVE";
         } else if (var1.equals(Color.ORANGE)) {
            var2 = "ORANGE";
         } else if (var1.equals(Color.PURPLE)) {
            var2 = "PURPLE";
         } else if (var1.equals(Color.RED)) {
            var2 = "RED";
         } else if (var1.equals(Color.SILVER)) {
            var2 = "SILVER";
         } else if (var1.equals(Color.TEAL)) {
            var2 = "TEAL";
         } else if (var1.equals(Color.WHITE)) {
            var2 = "WHITE";
         } else if (var1.equals(Color.YELLOW)) {
            var2 = "YELLOW";
         }

         return var2;
      }
   }

   public String transformItemStackToString(ItemStack var1) {
      String var2 = var1 != null ? var1.getType().name() : "";
      if (!var2.isEmpty()) {
         if (var1.getType().getMaxDurability() - var1.getDurability() != var1.getType().getMaxDurability()) {
            var2 = var2 + ":" + var1.getDurability();
         }

         var2 = var2 + " : " + var1.getAmount();
         if (var1.getItemMeta().getDisplayName() != null) {
            var2 = var2 + " : name:" + var1.getItemMeta().getDisplayName();
         }

         String var3;
         Iterator var4;
         if (var1.getItemMeta().getLore() != null && !var1.getItemMeta().getLore().isEmpty()) {
            for(var4 = var1.getItemMeta().getLore().iterator(); var4.hasNext(); var2 = var2 + " : lore:" + var3) {
               var3 = (String)var4.next();
            }
         }

         Enchantment var5;
         if (var1.getEnchantments() != null && !var1.getEnchantments().isEmpty()) {
            for(var4 = var1.getEnchantments().keySet().iterator(); var4.hasNext(); var2 = var2 + " : enchant:" + var5.getName().toUpperCase() + ":" + var1.getEnchantments().get(var5)) {
               var5 = (Enchantment)var4.next();
            }
         }

         if (var1.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta var6 = (LeatherArmorMeta)var1.getItemMeta();
            if (var6.getColor() != null) {
               var2 = var2 + " : dye:" + this.getColorName(var6.getColor());
            }
         }
      }

      return var2;
   }

   public void executeDatabaseUpdate(final CommandSender var1, final String var2, final BukkitRunnable var3, final String[] var4) {
      var1.sendMessage(this.plugin.msgs.prefix + ChatColor.YELLOW + "Couldn't find that player online, looking up the database...");
      if (this.plugin.config.useMySQL) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  if (!FileManager.this.plugin.mysql.getConnection().createStatement().executeQuery("select * from " + FileManager.this.plugin.config.tableprefix + " WHERE player_name= '" + var2 + "'").next()) {
                     var1.sendMessage(FileManager.this.plugin.msgs.prefix + "Couldn't find that player!");
                     return;
                  }

                  var4[0] = var2;
                  var3.runTaskAsynchronously(FileManager.this.plugin);
               } catch (SQLException var2x) {
                  var2x.printStackTrace();
               }

            }
         }).runTaskAsynchronously(this.plugin);
      } else {
         final FileConfiguration var5 = this.getConfig("players.yml");
         if (var5.getConfigurationSection("Players") == null || var5.getConfigurationSection("Players").getKeys(false).isEmpty()) {
            var1.sendMessage(this.plugin.msgs.prefix + "Couldn't find that player!");
            return;
         }

         final Iterator var6 = var5.getConfigurationSection("Players").getKeys(false).iterator();
         (new BukkitRunnable() {
            public void run() {
               int var1x = 0;

               while(var1x < 300 && var6.hasNext()) {
                  ++var1x;
                  String var2x = (String)var6.next();
                  String var3x = FileManager.this.plugin.config.UUID ? var5.getString("Players." + var2x + ".Name") : var2x;
                  if (var2.equalsIgnoreCase(var3x)) {
                     var4[0] = var2x;
                     var4[1] = var3x;
                     var3.run();
                     this.cancel();
                     return;
                  }
               }

               if (!var6.hasNext()) {
                  var1.sendMessage(FileManager.this.plugin.msgs.prefix + "Couldn't find that player!");
                  this.cancel();
               }

            }
         }).runTaskTimerAsynchronously(this.plugin, 0L, 1L);
      }

   }
}
