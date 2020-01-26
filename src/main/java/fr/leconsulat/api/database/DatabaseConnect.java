package fr.leconsulat.api.database;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseConnect {

    private Connection connection;
    private String host, database, username, password;

    public DatabaseConnect(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    synchronized void connectDatabase() throws SQLException {
        if(connection != null && !connection.isClosed()) return;
        Bukkit.getLogger().log(Level.WARNING, "Database connected");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
    }

    synchronized void disconnectDatabase() throws SQLException {
        connection.close();
        Bukkit.getLogger().log(Level.WARNING, "Database disconnected");;
    }

    Connection connection(){
        return connection;
    }
}
