package me.wazup.skywars;

import java.sql.Connection;
import java.sql.DriverManager;

class MySQL {
   public String table;
   private String url;
   private String username;
   private String password;
   private Connection connection;
   private String TABLE;
   String INSERT;
   String UPDATE;
   String UPDATE_STATS;
   String SELECT;
   String SELECTALL;
   String DELETE_PLAYER;

   public MySQL(Skywars var1, String var2, String var3, String var4, String var5, String var6, String var7) {
      this.url = "jdbc:mysql://" + var3 + ":" + var4 + "/" + var5;
      this.username = var6;
      this.password = var7;
      this.table = var2;
      this.TABLE = "create table if not exists " + var2 + " (player_uuid char(" + var1.getConfig().getInt("MySQL.max_uuid_size") + "), player_name varchar(16), stats varchar(90), inventory varchar(1300), selected varchar(50))";
      this.INSERT = "insert into " + var2 + " values(?, ?, ?, ?, ?)";
      this.UPDATE = "update " + var2 + " set player_name=?, stats=?, inventory=?, selected=? where " + (var1.config.useUUID ? "player_uuid" : "player_name") + "=?";
      this.UPDATE_STATS = "update " + var2 + " set stats=? where player_name=?";
      this.SELECT = "select * from " + var2 + " where " + (var1.config.useUUID ? "player_uuid" : "player_name") + "=?";
      this.SELECTALL = "select * from " + var2;
      this.DELETE_PLAYER = "delete from " + var2 + " where player_name=?";
   }

   public void close() {
      this.connection.close();
   }

   public void connect() {
      this.connection = DriverManager.getConnection(this.url, this.username, this.password);
   }

   public Connection getConnection() {
      if (this.connection == null || !this.connection.isValid(5)) {
         this.connect();
      }

      return this.connection;
   }

   public void setupTable() {
      Connection var1 = this.getConnection();
      var1.createStatement().executeUpdate(this.TABLE);
   }
}
