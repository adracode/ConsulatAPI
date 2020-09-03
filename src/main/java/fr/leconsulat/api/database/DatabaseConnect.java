package fr.leconsulat.api.database;

import fr.leconsulat.api.ConsulatAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class DatabaseConnect {
    
    private @Nullable Connection connection;
    private @NotNull String host, database, username, password;
    
    public DatabaseConnect(@NotNull String host, @NotNull String database, @NotNull String username, @NotNull String password){
        this.host = Objects.requireNonNull(host);
        this.database = Objects.requireNonNull(database);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
    }
    
    synchronized void connectDatabase() throws SQLException{
        if(connection != null && !connection.isClosed()){
            return;
        }
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Database connected");
    }
    
    synchronized void disconnectDatabase() throws SQLException{
        if(connection != null){
            connection.close();
            ConsulatAPI.getConsulatAPI().log(Level.INFO, "Database disconnected");
        } else {
            ConsulatAPI.getConsulatAPI().log(Level.WARNING, "Can't disconnect database: No current connection");
        }
    }
    
    @NotNull Connection connection(){
        return Objects.requireNonNull(connection, "No database connection");
    }
}
