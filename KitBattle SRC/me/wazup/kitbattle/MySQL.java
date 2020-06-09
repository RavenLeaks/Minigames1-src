package me.wazup.kitbattle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQL {
   private String url;
   private String username;
   private String password;
   String table;
   private Connection connection;

   public MySQL(String var1, String var2, String var3, String var4, String var5, String var6) {
      this.url = "jdbc:mysql://" + var2 + ":" + var3 + "/" + var4;
      this.username = var5;
      this.password = var6;
      this.table = var1;
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
      Statement var1 = this.getConnection().createStatement();
      var1.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.table + " (player_uuid VARCHAR(40), player_name VARCHAR(40), Coins int, Kills int, Deaths int, Exp int, Kitunlockers int, Kits VARCHAR(9999), Statistics VARCHAR(100))");
      if (!this.connection.getMetaData().getColumns((String)null, (String)null, this.table, "Statistics").next()) {
         var1.executeUpdate("ALTER TABLE " + this.table + " ADD COLUMN Statistics VARCHAR(100)");
      }

      var1.close();
   }
}
