package fr.leconsulat.api;

import fr.leconsulat.api.database.DatabaseManager;
import fr.leconsulat.api.listeners.ChunkChangeEvent;
import fr.leconsulat.api.listeners.ConnectionListeners;
import fr.leconsulat.api.listeners.PlayerMove;
import fr.leconsulat.api.ranks.RankDatabase;
import fr.leconsulat.api.runnable.KeepAlive;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class ConsulatAPI extends JavaPlugin {

    private static ConsulatAPI consulatAPI;
    private static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        consulatAPI = this;
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        Bukkit.getScheduler().runTaskTimer(this, new KeepAlive(), 0L, 20*60*5);

        Bukkit.getPluginManager().registerEvents(new ConnectionListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
    }

    @Override
    public void onDisable() {
        databaseManager.disconnect();
    }

    public static ConsulatAPI getConsulatAPI() { return consulatAPI; }

    public static Connection getDatabase(){
        return databaseManager.getDatabase();
    }
}
