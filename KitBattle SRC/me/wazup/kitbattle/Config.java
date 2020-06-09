package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Config {
   private Kitbattle plugin;
   boolean UUID;
   int StartingCoins;
   boolean ScoreboardEnabled;
   boolean KillCoinsContribution;
   boolean DoPlayersDropItemsOnDeath;
   boolean CanPlayersPickItemsOnGround;
   boolean CanPlayersDropItemsOnGround;
   Boolean NotifyWhenCooldownOff;
   boolean DoPlayersLoseHunger;
   int EarnedCoinsPerKill;
   ArrayList defaultKits;
   ArrayList possibleExp;
   ArrayList allowedCommands;
   ArrayList aliases;
   boolean BroadcastRankUp;
   boolean PurchaseableKitsArePermanent;
   boolean ShowRankInChat;
   int StartingAmountOfKitUnlockers;
   boolean respawnScreenOnDeath;
   int spawnTeleportCountdownSeconds;
   int spectateCountdownSeconds;
   double SellValue;
   boolean SendDeathMessageToEveryone;
   double SpongeBoostUpwards;
   double SpongeFallProtection;
   boolean SoupAutoDisappear;
   boolean FallDamageEnabled;
   boolean OpenKitsMenuOnRespawn;
   boolean SendKillstreaksToEveryone;
   boolean useMySQL;
   int ChallengeLives;
   int ChallengeRespawnProtectionSeconds;
   boolean AllowBuilding;
   boolean SoupDropSound;
   boolean SpongeLaunchSound;
   HashMap Killstreaks;
   HashMap Deathstreaks;
   List DeathstreakEndCommands;
   int leastDeathstreak;
   int UpdateTopSignsEveryInMinutes;
   int mapLoadDelay;
   HashMap modifiers;
   boolean bungeeMode;
   int shuffleEveryMinutes;
   int highestTimeShownBeforeShuffle;
   List timeShownBeforeShuffle;
   int tournamentsMinPlayers;
   int tournamentsGracePeriod;
   int tournamentsCountdownLength;
   int maxTournamentsTime;
   int celebrationLength;
   boolean tournamentsFireworksCelebration;
   List tournamentsWinnerCommands;
   List tournamentsTimeShownDuringCountdown;
   List tournamentsGracePeriodWarnings;
   List tournamentsEndWarnings;
   int tournamentsGracePeriodWarningsMax;
   int tournamentsEndWarningsMax;
   int challengesGraceLength;
   int maxChallengeTime;
   int challengeCelebrationLength;
   boolean challengesFireworksCelebration;
   List challengesGracePeriodWarnings;
   List challengesEndWarnings;
   int challengesGracePeriodWarningsMax;
   int challengesEndWarningsMax;
   List challengesWinnerCommands;
   boolean challengeKitLock;
   String challengeKit;
   boolean TrailsEnabled;
   int TrailsSize;
   int TrailsInterval;
   boolean scoreboardTitleAnimationEnabled;
   int scoreboardTitleAnimationInterval;
   List scoreboardTitleAnimationFrames;
   boolean titles_enabled;
   boolean actionbar_enabled;
   int titles_fadeIn;
   int titles_stay;
   int titles_fadeOut;
   List kitLoresOwned;
   List kitLoresShop;
   String tableprefix;
   String mysqlhost;
   String mysqlport;
   String mysqldatabase;
   String mysqlusername;
   String mysqlpassword;
   Material achievementLocked;
   Material achievementUnlocked;
   String achievementLockedLore;
   String achievementUnlockedLore;
   String achievementDescription;
   String achievementPrize;
   int KangarooCooldown;
   int FishermanCooldown;
   int DraculaCooldown;
   int DraculaLastsFor;
   int HadesCooldown;
   int HadesAmountOfDogs;
   int HadesDogsLastFor;
   int ThorCooldown;
   int ThorLightningDamage;
   int ThorStrikeRadius;
   int StomperStompRadius;
   int StomperMaxFallDamage;
   int StomperMaxDamageDealtWhenStompedWhileShifting;
   int SpidermanCooldown;
   int SpidermanWebsLastFor;
   int PrisonerCooldown;
   int PrisonerPrisonLastsFor;
   int ClimberCooldown;
   int ClimberTimeUntilChickenDisappear;
   int DragonCooldown;
   int DragonAmountOfBursts;
   int DragonDamageDealt;
   int DragonFireLastsFor;
   int DragonFireRange;
   int SouperCooldown;
   int PhantomCooldown;
   int PhantomFlightLastsFor;
   int TimelordCooldown;
   int TimelordFreezeRadius;
   int TimelordFreezeTime;
   int BurrowerCooldown;
   int BurrowerRoomLastsFor;
   int ZenCooldown;
   int ZenMaxRange;
   int ViperPoisonChance;
   PotionEffect poisonEffect;
   PotionEffect regenEffect;
   int MonkCooldown;
   int HulkCooldown;
   int HulkDamageDealt;
   int HulkDamageRadius;
   int RiderCooldown;
   int RiderHorseLastsFor;
   int SummonerCooldown;
   int SummonerGolemLastsFor;
   int SuicidalCooldown;
   int BaneCooldown;
   int BaneDuration;
   int SunderCooldown;
   int CentaurCooldown;
   int CentaurDamageRadius;
   int CentaurDamage;
   int BlinkerCooldown;
   String SignsPrefix;
   String JoinPrefix;
   String LeavePrefix;
   String SoupPrefix;
   String PotionsPrefix;
   ChatColor JoinLine3Color;

   public Config(Kitbattle var1) {
      this.poisonEffect = new PotionEffect(PotionEffectType.POISON, 200, 0);
      this.regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 200, 0);
      this.SignsPrefix = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "KB" + ChatColor.DARK_AQUA + "]";
      this.JoinPrefix = ChatColor.AQUA + "Join";
      this.LeavePrefix = ChatColor.AQUA + "Leave";
      this.SoupPrefix = ChatColor.AQUA + "Soup";
      this.PotionsPrefix = ChatColor.AQUA + "Potions";
      this.JoinLine3Color = ChatColor.BLACK;
      this.plugin = var1;
   }

   public void loadConfig() {
      FileConfiguration var1 = this.plugin.getConfig();
      this.UUID = var1.getBoolean("use-UUID");
      this.StartingCoins = var1.getInt("Starting-Coins");
      this.ScoreboardEnabled = var1.getBoolean("Scoreboard-Enabled");
      this.KillCoinsContribution = var1.getBoolean("Kill-Coins-Contribution");
      this.DoPlayersDropItemsOnDeath = var1.getBoolean("Do-Players-Drop-Items-On-Death");
      this.CanPlayersPickItemsOnGround = var1.getBoolean("Can-Players-Pick-Items-On-Ground");
      this.CanPlayersDropItemsOnGround = var1.getBoolean("Can-Players-Drop-Items-On-Ground");
      this.NotifyWhenCooldownOff = var1.getBoolean("Notify-When-Cooldown-Off");
      this.DoPlayersLoseHunger = var1.getBoolean("Do-Players-Lose-Hunger");
      this.EarnedCoinsPerKill = var1.getInt("Earned-Coins-Per-Kill");
      this.defaultKits = new ArrayList();
      Iterator var3 = var1.getStringList("Default-Kits").iterator();

      String var2;
      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.defaultKits.add(var2.toLowerCase());
      }

      this.possibleExp = new ArrayList();

      int var5;
      for(var5 = var1.getInt("Minimum-Exp-Per-Kill"); var5 < var1.getInt("Maximum-Exp-Per-Kill") + 1; ++var5) {
         this.possibleExp.add(var5);
      }

      this.allowedCommands = new ArrayList();
      var3 = var1.getStringList("Allowed-Commands").iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.allowedCommands.add(var2.toLowerCase());
      }

      this.aliases = new ArrayList();
      var3 = var1.getStringList("Aliases").iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.aliases.add(var2.toLowerCase());
      }

      this.mapLoadDelay = var1.getInt("Load-Delay");
      this.BroadcastRankUp = var1.getBoolean("Broadcast-Rank-Up");
      this.SellValue = var1.getDouble("Sell-Value");
      this.PurchaseableKitsArePermanent = var1.getBoolean("Purchaseable-Kits-Are-Permanent");
      this.ShowRankInChat = var1.getBoolean("Show-Rank-In-Chat");
      this.StartingAmountOfKitUnlockers = var1.getInt("Starting-Amount-Of-Kit-Unlockers");
      this.respawnScreenOnDeath = var1.getBoolean("Respawn-Screen-On-Death");
      this.spawnTeleportCountdownSeconds = var1.getInt("Spawn-Teleport-Countdown-Seconds");
      this.spectateCountdownSeconds = var1.getInt("Spectate-Countdown-Seconds");
      this.SendDeathMessageToEveryone = var1.getBoolean("Send-Death-Message-To-Everyone");
      this.SpongeBoostUpwards = var1.getDouble("Sponge-Boost-Upwards");
      this.SpongeFallProtection = this.SpongeBoostUpwards * this.SpongeBoostUpwards * 3.0D;
      this.SoupAutoDisappear = var1.getBoolean("Soup-Auto-Disappear");
      this.FallDamageEnabled = var1.getBoolean("Fall-Damage-Enabled");
      this.OpenKitsMenuOnRespawn = var1.getBoolean("Open-Kits-Menu-On-Respawn");
      this.ChallengeLives = var1.getInt("Challenge-Lives");
      this.ChallengeRespawnProtectionSeconds = var1.getInt("Challenge-Respawn-Protection-Seconds") + 1;
      this.AllowBuilding = var1.getBoolean("Allow-Building");
      this.SoupDropSound = var1.getBoolean("Soup-Drop-Sound");
      this.SpongeLaunchSound = var1.getBoolean("Sponge-Launch-Sound");
      this.modifiers = new HashMap();
      var3 = var1.getStringList("Modifiers").iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.modifiers.put(var2.split(" : ")[1], Double.valueOf(var2.split(" : ")[0]));
      }

      this.bungeeMode = var1.getBoolean("Bungee-Mode");
      this.shuffleEveryMinutes = var1.getInt("Shuffle-Every-In-Minutes");
      this.timeShownBeforeShuffle = var1.getIntegerList("Time-Shown-Before-Shuffle");
      this.highestTimeShownBeforeShuffle = 0;
      var3 = this.timeShownBeforeShuffle.iterator();

      while(var3.hasNext()) {
         var5 = (Integer)var3.next();
         if (var5 > this.highestTimeShownBeforeShuffle) {
            this.highestTimeShownBeforeShuffle = var5;
         }
      }

      this.tournamentsMinPlayers = var1.getInt("Tournaments.min-players");
      this.tournamentsGracePeriod = var1.getInt("Tournaments.grace-period");
      this.tournamentsCountdownLength = var1.getInt("Tournaments.countdown-length");
      this.maxTournamentsTime = var1.getInt("Tournaments.max-time");
      this.celebrationLength = var1.getInt("Tournaments.celebration-length");
      this.tournamentsFireworksCelebration = var1.getBoolean("Tournaments.fireworks-celebration");
      this.tournamentsWinnerCommands = var1.getStringList("Tournaments.winner-rewards");
      this.tournamentsTimeShownDuringCountdown = var1.getIntegerList("Tournaments.time-shown-during-countdown");
      this.tournamentsGracePeriodWarnings = var1.getIntegerList("Tournaments.grace-period-warnings");
      this.tournamentsEndWarnings = var1.getIntegerList("Tournaments.end-warnings");
      this.tournamentsGracePeriodWarningsMax = 0;
      var3 = this.tournamentsGracePeriodWarnings.iterator();

      while(var3.hasNext()) {
         var5 = (Integer)var3.next();
         if (var5 > this.tournamentsGracePeriodWarningsMax) {
            this.tournamentsGracePeriodWarningsMax = var5;
         }
      }

      this.tournamentsEndWarningsMax = 0;
      var3 = this.tournamentsEndWarnings.iterator();

      while(var3.hasNext()) {
         var5 = (Integer)var3.next();
         if (var5 > this.tournamentsEndWarningsMax) {
            this.tournamentsEndWarningsMax = var5;
         }
      }

      this.maxChallengeTime = var1.getInt("Challenges.max-time");
      this.challengesGraceLength = var1.getInt("Challenges.grace-length");
      this.challengeCelebrationLength = var1.getInt("Challenges.celebration-length");
      this.challengesFireworksCelebration = var1.getBoolean("Tournaments.fireworks-celebration");
      this.challengesGracePeriodWarnings = var1.getIntegerList("Challenges.grace-period-warnings");
      this.challengesEndWarnings = var1.getIntegerList("Challenges.end-warnings");
      this.challengesWinnerCommands = var1.getStringList("Challenges.winner-rewards");
      this.challengeKitLock = var1.getBoolean("Challenges.kit-lock.enabled");
      this.challengeKit = var1.getString("Challenges.kit-lock.challenge-kit");
      this.challengesGracePeriodWarningsMax = 0;
      var3 = this.challengesGracePeriodWarnings.iterator();

      while(var3.hasNext()) {
         var5 = (Integer)var3.next();
         if (var5 > this.challengesGracePeriodWarningsMax) {
            this.challengesGracePeriodWarningsMax = var5;
         }
      }

      this.challengesEndWarningsMax = 0;
      var3 = this.challengesEndWarnings.iterator();

      while(var3.hasNext()) {
         var5 = (Integer)var3.next();
         if (var5 > this.challengesEndWarningsMax) {
            this.challengesEndWarningsMax = var5;
         }
      }

      this.scoreboardTitleAnimationEnabled = var1.getBoolean("Scoreboard-Title-Animation.enabled");
      this.scoreboardTitleAnimationInterval = var1.getInt("Scoreboard-Title-Animation.interval");
      this.scoreboardTitleAnimationFrames = new ArrayList();
      var3 = var1.getStringList("Scoreboard-Title-Animation.frames").iterator();

      while(var3.hasNext()) {
         var2 = (String)var3.next();
         this.scoreboardTitleAnimationFrames.add(ChatColor.translateAlternateColorCodes('&', var2));
      }

      if (this.scoreboardTitleAnimationFrames.size() < 2) {
         this.scoreboardTitleAnimationEnabled = false;
         Bukkit.getConsoleSender().sendMessage(this.plugin.kb + "Scoreboard animation was disabled because there are not enough amount of frames!");
      } else if (this.scoreboardTitleAnimationEnabled) {
         Bukkit.getConsoleSender().sendMessage(this.plugin.kb + "Scoreboard title animation has been enabled and it contains " + ChatColor.AQUA + this.scoreboardTitleAnimationFrames.size() + ChatColor.GRAY + " frame(s)!");
      }

      this.Killstreaks = new HashMap();
      if (var1.getConfigurationSection("Killstreaks") == null) {
         var1.set("Killstreaks.3.Commands-Executed", Arrays.asList("kb coins add %player% 20"));
         var1.set("Killstreaks.5.Commands-Executed", Arrays.asList("kb coins add %player% 30"));
         var1.set("Killstreaks.10.Commands-Executed", Arrays.asList("kb coins add %player% 50"));
         var1.set("Killstreaks.20.Commands-Executed", Arrays.asList("kb coins add %player% 100"));
         var1.set("Killstreaks.30.Commands-Executed", Arrays.asList("kb coins add %player% 200"));
         this.plugin.saveConfig();
      }

      if (var1.getBoolean("Killstreaks-Enabled")) {
         var3 = var1.getConfigurationSection("Killstreaks").getKeys(false).iterator();

         while(var3.hasNext()) {
            var2 = (String)var3.next();
            this.Killstreaks.put(Integer.valueOf(var2), var1.getStringList("Killstreaks." + var2 + ".Commands-Executed"));
         }
      }

      this.kitLoresOwned = var1.getStringList("Kit-Lores.Owned");
      this.kitLoresShop = var1.getStringList("Kit-Lores.Shop");
      this.TrailsEnabled = var1.getBoolean("Trails.enabled");
      this.TrailsSize = var1.getInt("Trails.size");
      this.TrailsInterval = var1.getInt("Trails.interval");
      this.Deathstreaks = new HashMap();
      this.leastDeathstreak = Integer.MAX_VALUE;
      if (var1.getConfigurationSection("Deathstreaks") == null) {
         var1.set("Deathstreaks.5.Commands-Executed", Arrays.asList("effect %player% resistance 9999 0"));
         var1.set("Deathstreaks.8.Commands-Executed", Arrays.asList("effect %player% resistance 9999 0", "effect %player% regeneration 9999 1"));
         var1.set("Deathstreaks.10.Commands-Executed", Arrays.asList("effect %player% resistance 9999 0", "effect %player% regeneration 9999 1", "effect %player% strength 9999 0", "effect %player% speed 9999 0"));
         this.plugin.saveConfig();
      }

      if (var1.getBoolean("Deathstreaks-Enabled")) {
         var3 = var1.getConfigurationSection("Deathstreaks").getKeys(false).iterator();

         while(var3.hasNext()) {
            var2 = (String)var3.next();
            int var4 = Integer.valueOf(var2);
            this.Deathstreaks.put(var4, var1.getStringList("Deathstreaks." + var2 + ".Commands-Executed"));
            if (var4 < this.leastDeathstreak) {
               this.leastDeathstreak = var4;
            }
         }
      }

      this.DeathstreakEndCommands = var1.getStringList("Deathstreak-End-Commands");
      this.SendKillstreaksToEveryone = var1.getBoolean("Send-Killstreaks-To-Everyone");
      this.UpdateTopSignsEveryInMinutes = var1.getInt("Update-Top-Signs-Every-In-Minutes");
      this.useMySQL = var1.getBoolean("use-mysql");
      this.tableprefix = var1.getString("table-prefix");
      this.mysqlhost = var1.getString("mysql-host");
      this.mysqlport = var1.getString("mysql-port");
      this.mysqldatabase = var1.getString("mysql-database");
      this.mysqlusername = var1.getString("mysql-username");
      this.mysqlpassword = var1.getString("mysql-password");
      boolean var6 = Bukkit.getPluginManager().getPlugin("TitleManager") != null && Bukkit.getPluginManager().getPlugin("TitleManager").isEnabled();
      if (var6) {
         Bukkit.getConsoleSender().sendMessage(this.plugin.kb + "TitleManager has been detected! TitleManager features are now accessible..");
         this.titles_enabled = var1.getBoolean("Titles.enabled");
         this.titles_fadeIn = var1.getInt("Titles.fadeIn");
         this.titles_stay = var1.getInt("Titles.stay");
         this.titles_fadeOut = var1.getInt("Titles.fadeOut");
         this.actionbar_enabled = var1.getBoolean("Action-Bar-Enabled");
      } else {
         this.titles_enabled = false;
         this.actionbar_enabled = false;
      }

      FileConfiguration var7 = this.plugin.fileManager.getConfig("achievements.yml");
      this.achievementLocked = Material.getMaterial(var7.getString("Styling.Locked-Material"));
      this.achievementUnlocked = Material.getMaterial(var7.getString("Styling.Unlocked-Material"));
      this.achievementLockedLore = ChatColor.translateAlternateColorCodes('&', var7.getString("Styling.Locked-Lore"));
      this.achievementUnlockedLore = ChatColor.translateAlternateColorCodes('&', var7.getString("Styling.Unlocked-Lore"));
      this.achievementDescription = ChatColor.translateAlternateColorCodes('&', var7.getString("Styling.Name"));
      this.achievementPrize = ChatColor.translateAlternateColorCodes('&', var7.getString("Styling.Prize"));
   }

   public void loadAbilities() {
      FileConfiguration var1 = this.plugin.fileManager.getConfig("abilities.yml");
      this.KangarooCooldown = var1.getInt("Abilities.Kangaroo.Cooldown");
      this.FishermanCooldown = var1.getInt("Abilities.Fisherman.Cooldown");
      this.DraculaCooldown = var1.getInt("Abilities.Dracula.Cooldown");
      this.DraculaLastsFor = var1.getInt("Abilities.Dracula.Lasts-For");
      this.HadesCooldown = var1.getInt("Abilities.Hades.Cooldown");
      this.HadesAmountOfDogs = var1.getInt("Abilities.Hades.Amount-Of-Dogs");
      this.HadesDogsLastFor = var1.getInt("Abilities.Hades.Dogs-Last-For") * 20;
      this.ThorCooldown = var1.getInt("Abilities.Thor.Cooldown");
      this.ThorLightningDamage = var1.getInt("Abilities.Thor.Lightning-Damage") * 2;
      this.ThorStrikeRadius = var1.getInt("Abilities.Thor.Strike-Radius");
      this.StomperStompRadius = var1.getInt("Abilities.Stomper.Stomp-Radius");
      this.StomperMaxFallDamage = var1.getInt("Abilities.Stomper.Max-Fall-Damage") * 2;
      this.StomperMaxDamageDealtWhenStompedWhileShifting = var1.getInt("Abilities.Stomper.Max-Damage-Dealt-When-Stomped-While-Shifting") * 2;
      this.SpidermanCooldown = var1.getInt("Abilities.Spiderman.Cooldown");
      this.SpidermanWebsLastFor = var1.getInt("Abilities.Spiderman.Webs-Last-For") * 20;
      this.PrisonerCooldown = var1.getInt("Abilities.Prisoner.Cooldown");
      this.PrisonerPrisonLastsFor = var1.getInt("Abilities.Prisoner.Prison-Lasts-For") * 20;
      this.ClimberCooldown = var1.getInt("Abilities.Climber.Cooldown");
      this.ClimberTimeUntilChickenDisappear = var1.getInt("Abilities.Climber.Time-Until-Chicken-Disappear") * 20;
      this.DragonCooldown = var1.getInt("Abilities.Dragon.Cooldown");
      this.DragonAmountOfBursts = var1.getInt("Abilities.Dragon.Amount-Of-Bursts");
      this.DragonDamageDealt = var1.getInt("Abilities.Dragon.Damage-Dealt") * 2;
      this.DragonFireLastsFor = var1.getInt("Abilities.Dragon.Fire-Lasts-For") * 20;
      this.DragonFireRange = var1.getInt("Abilities.Dragon.Fire-Range");
      this.PhantomCooldown = var1.getInt("Abilities.Phantom.Cooldown");
      this.PhantomFlightLastsFor = var1.getInt("Abilities.Phantom.Flight-Lasts-For");
      this.TimelordCooldown = var1.getInt("Abilities.Timelord.Cooldown");
      this.TimelordFreezeRadius = var1.getInt("Abilities.Timelord.Freeze-Radius");
      this.TimelordFreezeTime = var1.getInt("Abilities.Timelord.Freeze-Time") * 20;
      this.BurrowerCooldown = var1.getInt("Abilities.Burrower.Cooldown");
      this.BurrowerRoomLastsFor = var1.getInt("Abilities.Burrower.Room-Lasts-For") * 20;
      this.ZenCooldown = var1.getInt("Abilities.Zen.Cooldown");
      this.ZenMaxRange = var1.getInt("Abilities.Zen.Max-Range");
      this.ViperPoisonChance = Integer.valueOf(var1.getString("Abilities.Viper.Poison-Chance").replace("%", ""));
      this.poisonEffect = new PotionEffect(PotionEffectType.POISON, var1.getInt("Abilities.Viper.Poison-Lasts-For") * 20, var1.getInt("Abilities.Viper.Poison-Level") - 1);
      this.MonkCooldown = var1.getInt("Abilities.Monk.Cooldown");
      this.HulkCooldown = var1.getInt("Abilities.Hulk.Cooldown");
      this.HulkDamageDealt = var1.getInt("Abilities.Hulk.Damage-Dealt") * 2;
      this.HulkDamageRadius = var1.getInt("Abilities.Hulk.Damage-Radius");
      this.RiderCooldown = var1.getInt("Abilities.Rider.Cooldown");
      this.RiderHorseLastsFor = var1.getInt("Abilities.Rider.Horse-Lasts-For") * 20;
      this.SummonerCooldown = var1.getInt("Abilities.Summoner.Cooldown");
      this.SummonerGolemLastsFor = var1.getInt("Abilities.Summoner.Golem-Lasts-For") * 20;
      this.SuicidalCooldown = var1.getInt("Abilities.Suicidal.Cooldown");
      this.SouperCooldown = var1.getInt("Abilities.Souper.Cooldown");
      this.BaneCooldown = var1.getInt("Abilities.Bane.Cooldown");
      this.BaneDuration = var1.getInt("Abilities.Bane.Duration") * 20;
      this.SunderCooldown = var1.getInt("Abilities.Sunder.Cooldown");
      this.CentaurCooldown = var1.getInt("Abilities.Centaur.Cooldown");
      this.CentaurDamageRadius = var1.getInt("Abilities.Centaur.Damage-Radius");
      this.CentaurDamage = var1.getInt("Abilities.Centaur.Damage");
      this.regenEffect = new PotionEffect(PotionEffectType.REGENERATION, var1.getInt("Abilities.Centaur.Regen-Duration") * 20, var1.getInt("Abilities.Centaur.Regen-Level") - 1);
      this.BlinkerCooldown = var1.getInt("Abilities.Blinker.Cooldown");
   }

   public void loadSignPrefixes() {
      FileConfiguration var1 = this.plugin.fileManager.getConfig("signs.yml");
      this.SignsPrefix = ChatColor.translateAlternateColorCodes('&', var1.getString("Signs-Prefix"));
      this.JoinPrefix = ChatColor.translateAlternateColorCodes('&', var1.getString("Join-Prefix"));
      this.LeavePrefix = ChatColor.translateAlternateColorCodes('&', var1.getString("Leave-Prefix"));
      this.SoupPrefix = ChatColor.translateAlternateColorCodes('&', var1.getString("Soup-Prefix"));
      this.PotionsPrefix = ChatColor.translateAlternateColorCodes('&', var1.getString("Potions-Prefix"));
      this.JoinLine3Color = ChatColor.getByChar(var1.getString("Join-Line-3-Color").replace("&", ""));
   }
}
