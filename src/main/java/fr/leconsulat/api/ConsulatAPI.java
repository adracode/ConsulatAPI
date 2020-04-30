package fr.leconsulat.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.TestCommand;
import fr.leconsulat.api.database.DatabaseManager;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.gui.exemples.TestGui;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.runnable.KeepAlive;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;

public class ConsulatAPI extends JavaPlugin {
    
    private static ConsulatAPI consulatAPI;
    
    private ProtocolManager protocolManager;
    private CPlayerManager playerManager;
    private DatabaseManager databaseManager;
    private GuiManager guiManager;
    private CommandManager commandManager;
    private File log;
    private boolean debug = false;
    
    @Override
    public void onEnable(){
        if(consulatAPI != null){
            return;
        }
        consulatAPI = this;
        log = new File(this.getDataFolder(), "log.txt");
        saveDefaultConfig();
        this.debug = getConfig().getBoolean("debug", false);
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new CPlayerManager();
        commandManager = new CommandManager(this);
        guiManager = new GuiManager(this);
        if(isDebug()){
            commandManager.addCommand(new TestCommand());
            guiManager.addRootGui("yes", new TestGui());
        }
        registerEvents();
        Bukkit.getScheduler().runTaskTimer(this, new KeepAlive(), 0L, 20 * 60 * 5);
    }
    
    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(playerManager, this);
    }
    
    @Override
    public void onDisable(){
        if(isDebug()){
            for(Player player : Bukkit.getOnlinePlayers()){
                getServer().getPluginManager().callEvent(new PlayerQuitEvent(player, ""));
            }
        }
        databaseManager.disconnect();
    }
    
    public boolean isDebug(){
        return debug;
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
    
    public ProtocolManager getProtocolManager(){
        return protocolManager;
    }
}
