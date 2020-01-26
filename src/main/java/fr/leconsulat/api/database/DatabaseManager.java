package fr.leconsulat.api.database;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private ConsulatAPI consulatAPI;
    private DatabaseConnect databaseConnect;

    public Connection database;

    public DatabaseManager(ConsulatAPI consulatAPI) {
        this.consulatAPI = consulatAPI;
    }

    public void connect(){
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
