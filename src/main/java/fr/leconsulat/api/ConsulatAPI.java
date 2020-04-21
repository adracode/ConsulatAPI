package fr.leconsulat.api;

import fr.leconsulat.api.database.DatabaseManager;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.runnable.KeepAlive;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;

public class ConsulatAPI extends JavaPlugin {
    
    private static ConsulatAPI consulatAPI;
    
    private CPlayerManager playerManager;
    private DatabaseManager databaseManager;
    private File log;
    
    @Override
    public void onEnable(){
        if(consulatAPI != null){
            return;
        }
        consulatAPI = this;
        log = new File(this.getDataFolder(), "log.txt");
        saveDefaultConfig();
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        playerManager = new CPlayerManager();
        registerEvents();
        Bukkit.getScheduler().runTaskTimer(this, new KeepAlive(), 0L, 20 * 60 * 5);
    }
    
    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(playerManager, this);
    }
    
    @Override
    public void onDisable(){
        databaseManager.disconnect();
    }
    
    public static ConsulatAPI getConsulatAPI(){
        return consulatAPI;
    }
    
    public static Connection getDatabase(){
        return getConsulatAPI().databaseManager.getDatabase();
    }
    
    public void log(Level level, Object message){
        this.getLogger().log(level, message.toString());
    }
    
    public void logFile(Object message){
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                BufferedWriter bf = new BufferedWriter(new FileWriter(log, true));
                bf.write(System.currentTimeMillis() + " " + message.toString() + '\n');
                bf.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        });
    }
}
