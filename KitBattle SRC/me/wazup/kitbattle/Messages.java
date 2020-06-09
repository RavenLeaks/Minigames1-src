package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
   String prefix;
   String nopermission;
   String noconsole;
   String pluginReloadKickMessage;
   String UnknownCommand;
   String CommandDisabled;
   String UnknownMap;
   String PlayerJoinMap;
   String NotInAGame;
   String NoAvailableMaps;
   String MapDeletedSendToAnotherMap;
   String MapDeletedKick;
   String PlayerReceiveKitUnlocker;
   String PlayerLeave;
   String PlayerStatModificationThroughCommand;
   String MapSwitch;
   String MapSwitchCountDown;
   String PlayerDeathMessage;
   String PlayerEarnExp;
   String PlayerKill;
   String PlayerSuicideMessage;
   String JoinDenied;
   String AlreadyUnlockedAllKits;
   String PlayerOpenKitUnlocker;
   String PlayerWinKit;
   String PlayerSelectKit;
   String PlayerPurchaseKit;
   String PlayerAlreadyInMap;
   String NotEnoughCoins;
   String CantUseCommands;
   String CantUseCharacter;
   String PlayerRankUpPublicMessage;
   String PlayerDerankPublicMessage;
   String StillOnCooldown;
   String UseAbilityDeny;
   String PrisonerNoSpace;
   String MustHaveSelectedKit;
   String PlayerResetKit;
   String PlayerGetKillstreakSelfMessage;
   String PlayerGetKillstreakAnnouncement;
   String PhantomFlyTimeLeft;
   String PhantomNotHacking;
   String DraculaSuckWarning;
   String DraculaReceiveEffect;
   String BurrowerNoSpace;
   String ZenNoPlayersFound;
   String LoadStatsFailureMySql;
   String KitDisabled;
   String NoPermissionForKit;
   String PlayerNotFound;
   String KitAlreadyUnlocked;
   String MovementNotAllowed;
   String MovementOccur;
   String SpectatorModeNotSupported;
   String SpectatorModeEnable;
   String SpectatorModeDisable;
   String AlreadySelectedKit;
   String UnknownKit;
   String KitSelectDeny;
   String NoAvailableSpawnpoint;
   String MapDisabled;
   String AbilityUseDeny;
   String TournamentCountdown;
   String TournamentCancelDueToPlayers;
   String TournamentCancelDueToMaps;
   String TournamentPlayerLeave;
   String TournamentPlayerWin;
   String TournamentStart;
   String GraceWarning;
   String GraceEnd;
   String TournamentEndWarning;
   String TournamentPlayerEliminated;
   String NoUpgrades;
   String CooldownRemove;
   String TeamColorNotification;
   String ChallengeEndWarning;
   String ChallengePlayerKilled;
   String ChallengePlayerEliminated;
   String ChallengePlayerLeave;
   String ChallengePlayerWin;
   String DeathstreakBuff;
   String DeathstreakEnd;
   String BaneCurse;
   String SunderSwap;
   String CentaurStrike;
   String PlayerBlink;
   String TrailSelect;
   ArrayList killMessages;
   String fallMessage;
   String lavaMessage;
   String fireMessage;
   String explodeMessage;
   String unknownMessage;
   String scoreboard_title;
   String scoreboard_challenge_title;
   String scoreboard_map;
   String scoreboard_players;
   String scoreboard_team1;
   String scoreboard_team2;
   String[] defaultScoreboard;
   public HashMap inventories;

   public void loadMessages(FileConfiguration var1) {
      this.prefix = this.c(var1, "prefix");
      this.nopermission = this.c(var1, "Messages.No-Permission");
      this.noconsole = this.c(var1, "Messages.No-Console");
      this.pluginReloadKickMessage = this.c(var1, "Messages.Plugin-Reload-Kick-Message");
      this.UnknownCommand = this.c(var1, "Messages.Unknown-Command");
      this.CommandDisabled = this.c(var1, "Messages.Command-Disabled");
      this.UnknownMap = this.c(var1, "Messages.Unknown-Map");
      this.PlayerJoinMap = this.c(var1, "Messages.Player-Join-Map");
      this.NotInAGame = this.c(var1, "Messages.Not-In-A-Game");
      this.NoAvailableMaps = this.c(var1, "Messages.No-Available-Maps");
      this.MapDeletedSendToAnotherMap = this.c(var1, "Messages.Map-Deleted-Send-To-Another-Map");
      this.MapDeletedKick = this.c(var1, "Messages.Map-Deleted-Kick");
      this.PlayerReceiveKitUnlocker = this.c(var1, "Messages.Player-Receive-KitUnlocker");
      this.PlayerLeave = this.c(var1, "Messages.Player-Leave");
      this.MapSwitch = this.c(var1, "Messages.Map-Switch");
      this.MapSwitchCountDown = this.c(var1, "Messages.Map-Switch-Count-Down");
      this.killMessages = new ArrayList();
      Iterator var3 = var1.getStringList("Player-Death-By-Player").iterator();

      while(var3.hasNext()) {
         String var2 = (String)var3.next();
         this.killMessages.add(ChatColor.translateAlternateColorCodes('&', var2));
      }

      this.fallMessage = this.c(var1, "Player-Death-By-Falldamage");
      this.lavaMessage = this.c(var1, "Player-Death-By-Lava");
      this.fireMessage = this.c(var1, "Player-Death-By-Fire");
      this.explodeMessage = this.c(var1, "Player-Death-By-Explosion");
      this.unknownMessage = this.c(var1, "Player-Death-By-Unknown");
      this.PlayerEarnExp = this.c(var1, "Messages.Player-Earn-Exp");
      this.PlayerKill = this.c(var1, "Messages.Player-Kill");
      this.PlayerDeathMessage = this.c(var1, "Messages.Player-Death-Message");
      this.PlayerSuicideMessage = this.c(var1, "Messages.Player-Suicide-Message");
      this.JoinDenied = this.c(var1, "Messages.Join-Denied");
      this.AlreadyUnlockedAllKits = this.c(var1, "Messages.Already-Unlocked-All-Kits");
      this.PlayerOpenKitUnlocker = this.c(var1, "Messages.Player-Open-KitUnlocker");
      this.PlayerWinKit = this.c(var1, "Messages.Player-Win-Kit");
      this.PlayerSelectKit = this.c(var1, "Messages.Player-Select-Kit");
      this.PlayerPurchaseKit = this.c(var1, "Messages.Player-Purchase-Kit");
      this.PlayerAlreadyInMap = this.c(var1, "Messages.Player-Already-In-Map");
      this.NotEnoughCoins = this.c(var1, "Messages.Not-Enough-Coins");
      this.CantUseCommands = this.c(var1, "Messages.Cant-Use-Commands");
      this.CantUseCharacter = this.c(var1, "Messages.Cant-Use-Character");
      this.PlayerStatModificationThroughCommand = this.c(var1, "Messages.Player-Stat-Modification-Through-Command");
      this.PlayerRankUpPublicMessage = this.c(var1, "Messages.Player-Rank-Up-Public-Message");
      this.PlayerDerankPublicMessage = this.c(var1, "Messages.Player-Derank-Public-Message");
      this.StillOnCooldown = this.c(var1, "Messages.Still-On-Cooldown");
      this.UseAbilityDeny = this.c(var1, "Messages.Use-Ability-Deny");
      this.PrisonerNoSpace = this.c(var1, "Messages.Prisoner-No-Space");
      this.MustHaveSelectedKit = this.c(var1, "Messages.Must-Have-Selected-Kit");
      this.PlayerResetKit = this.c(var1, "Messages.Player-Reset-Kit");
      this.PlayerGetKillstreakSelfMessage = this.c(var1, "Messages.Player-Get-Killstreak-Self-Message");
      this.PlayerGetKillstreakAnnouncement = this.c(var1, "Messages.Player-Get-Killstreak-Announcement");
      this.PhantomFlyTimeLeft = this.c(var1, "Messages.Phantom-Fly-Time-Left");
      this.PhantomNotHacking = this.c(var1, "Messages.Phantom-Not-Hacking");
      this.BurrowerNoSpace = this.c(var1, "Messages.Burrower-No-Space");
      this.DraculaSuckWarning = this.c(var1, "Messages.Dracula-Suck-Warning");
      this.DraculaReceiveEffect = this.c(var1, "Messages.Dracula-Receive-Effect");
      this.ZenNoPlayersFound = this.c(var1, "Messages.Zen-No-Players-Found");
      this.LoadStatsFailureMySql = this.c(var1, "Messages.Load-Stats-Failure-MySql");
      this.KitDisabled = this.c(var1, "Messages.Kit-Disabled");
      this.NoPermissionForKit = this.c(var1, "Messages.No-Permission-For-Kit");
      this.PlayerNotFound = this.c(var1, "Messages.Player-Not-Found");
      this.KitAlreadyUnlocked = this.c(var1, "Messages.Kit-Already-Unlocked");
      this.MovementNotAllowed = this.c(var1, "Messages.Movement-Not-Allowed");
      this.MovementOccur = this.c(var1, "Messages.Movement-Occur");
      this.SpectatorModeNotSupported = this.c(var1, "Messages.Spectator-Mode-Not-Supported");
      this.SpectatorModeEnable = this.c(var1, "Messages.Spectator-Mode-Enable");
      this.SpectatorModeDisable = this.c(var1, "Messages.Spectator-Mode-Disable");
      this.AlreadySelectedKit = this.c(var1, "Messages.Already-Selected-Kit");
      this.UnknownKit = this.c(var1, "Messages.Unknown-Kit");
      this.KitSelectDeny = this.c(var1, "Messages.Kit-Select-Deny");
      this.NoAvailableSpawnpoint = this.c(var1, "Messages.No-Available-Spawnpoint");
      this.MapDisabled = this.c(var1, "Messages.Map-Disabled");
      this.AbilityUseDeny = this.c(var1, "Messages.Ability-Use-Deny");
      this.TournamentCountdown = this.c(var1, "Messages.Tournament-Countdown");
      this.TournamentCancelDueToPlayers = this.c(var1, "Messages.Tournament-Cancel-Due-To-Players");
      this.TournamentCancelDueToMaps = this.c(var1, "Messages.Tournament-Cancel-Due-To-Maps");
      this.TournamentPlayerLeave = this.c(var1, "Messages.Tournament-Player-Leave");
      this.TournamentPlayerWin = this.c(var1, "Messages.Tournament-Player-Win");
      this.TournamentStart = this.c(var1, "Messages.Tournament-Start");
      this.GraceWarning = this.c(var1, "Messages.Grace-Warning");
      this.GraceEnd = this.c(var1, "Messages.Grace-End");
      this.TournamentEndWarning = this.c(var1, "Messages.Tournament-End-Warning");
      this.TournamentPlayerEliminated = this.c(var1, "Messages.Tournament-Player-Eliminated");
      this.NoUpgrades = this.c(var1, "Messages.No-Upgrades");
      this.CooldownRemove = this.c(var1, "Messages.Cooldown-Remove");
      this.TeamColorNotification = this.c(var1, "Messages.Team-Color-Notification");
      this.ChallengeEndWarning = this.c(var1, "Messages.Challenge-End-Warning");
      this.ChallengePlayerKilled = this.c(var1, "Messages.Challenge-Player-Killed");
      this.ChallengePlayerEliminated = this.c(var1, "Messages.Challenge-Player-Eliminated");
      this.ChallengePlayerLeave = this.c(var1, "Messages.Challenge-Player-Leave");
      this.ChallengePlayerWin = this.c(var1, "Messages.Challenge-Player-Win");
      this.DeathstreakBuff = this.c(var1, "Messages.Deaathsreak-Receive");
      this.DeathstreakEnd = this.c(var1, "Messages.Deathstreak-End");
      this.BaneCurse = this.c(var1, "Messages.Bane-Curse");
      this.SunderSwap = this.c(var1, "Messages.Sunder-Swap");
      this.CentaurStrike = this.c(var1, "Messages.Centaur-Strike");
      this.PlayerBlink = this.c(var1, "Messages.Player-Blink");
      this.TrailSelect = this.c(var1, "Messages.Trail-Select");
      this.scoreboard_title = this.c(var1, "Scoreboard.title");
      this.scoreboard_challenge_title = this.c(var1, "Scoreboard.challenge-title");
      this.scoreboard_map = this.c(var1, "Scoreboard.map");
      this.scoreboard_players = this.c(var1, "Scoreboard.players");
      this.scoreboard_team1 = this.c(var1, "Scoreboard.team1");
      this.scoreboard_team2 = this.c(var1, "Scoreboard.team2");
      this.defaultScoreboard = new String[var1.getStringList("Scoreboard.content").size()];
      int var5 = 0;

      Iterator var4;
      String var6;
      for(var4 = var1.getStringList("Scoreboard.content").iterator(); var4.hasNext(); this.defaultScoreboard[var5++] = ChatColor.translateAlternateColorCodes('&', var6)) {
         var6 = (String)var4.next();
      }

      this.inventories = new HashMap();
      var4 = var1.getConfigurationSection("Inventories").getKeys(false).iterator();

      while(var4.hasNext()) {
         var6 = (String)var4.next();
         this.inventories.put(var6, ChatColor.translateAlternateColorCodes('&', var1.getString("Inventories." + var6)));
      }

   }

   private String c(FileConfiguration var1, String var2) {
      return ChatColor.translateAlternateColorCodes('&', var1.getString(var2));
   }

   private void b(FileConfiguration var1, String var2, String var3) {
      if (!var1.contains(var2)) {
         var1.set(var2, var3);
      }

   }
}
