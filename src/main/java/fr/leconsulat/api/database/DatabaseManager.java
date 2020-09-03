package fr.leconsulat.api.database;

import fr.leconsulat.api.ConsulatAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class DatabaseManager {
    
    private @Nullable Connection database;
    private @Nullable DatabaseConnect databaseConnect;
    
    public void connect(){
        FileConfiguration config = ConsulatAPI.getConsulatAPI().getConfig();
        String host = Objects.requireNonNull(config.getString("host"), "Host");
        String database = Objects.requireNonNull(config.getString("database"), "Database");
        String username = Objects.requireNonNull(config.getString("username"), "Username");
        String password = Objects.requireNonNull(config.getString("password"), "Password");
        databaseConnect = new DatabaseConnect(host, database, username, password);
        try {
            databaseConnect.connectDatabase();
        } catch(SQLException e){
            e.printStackTrace();
            return;
        }
        this.database = databaseConnect.connection();
    }
    
    public void disconnect(){
        try {
            if(databaseConnect != null){
                databaseConnect.disconnectDatabase();
            } else {
                ConsulatAPI.getConsulatAPI().log(Level.WARNING, "Can't disconnect database: No connection");
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public @NotNull Connection getDatabase(){
        return Objects.requireNonNull(database, "No database connection");
    }
}
