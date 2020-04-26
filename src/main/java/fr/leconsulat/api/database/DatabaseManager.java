package fr.leconsulat.api.database;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private DatabaseConnect databaseConnect;

    public Connection database;

    public DatabaseManager() {
            }

    public void connect(){
        ConsulatAPI consulatAPI = ConsulatAPI.getConsulatAPI();
        String host = consulatAPI.getConfig().getString("host");
        String databaseString = consulatAPI.getConfig().getString("database");
        String username = consulatAPI.getConfig().getString("username");
        String password = consulatAPI.getConfig().getString("password");

       databaseConnect = new DatabaseConnect(host, databaseString, username, password);
       try {
        databaseConnect.connectDatabase();
       } catch (SQLException e) {
            e.printStackTrace();
       }
       database = databaseConnect.connection();
    }

    public void disconnect(){
        try {
            databaseConnect.disconnectDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getDatabase() {
        return database;
    }
}
