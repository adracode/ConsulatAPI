package fr.leconsulat.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import fr.leconsulat.api.channel.ChannelManager;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.commands.*;
import fr.leconsulat.api.database.DatabaseManager;
import fr.leconsulat.api.database.SaveManager;
import fr.leconsulat.api.events.EventManager;
import fr.leconsulat.api.events.PostInitEvent;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.nms.api.NMS;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.runnable.KeepAlive;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;

public class ConsulatAPI extends JavaPlugin implements Listener {
    
    private static ConsulatAPI consulatAPI;
    
    private Object dedicatedServer;
    
    private NMS nms;
    private ProtocolManager protocolManager;
    private CPlayerManager playerManager;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private File log;
    private boolean debug = false;
    private boolean development = false;
    
    private int lastTimeTick = 50;
    
    @Override
    public void onEnable(){
        if(consulatAPI != null){
            return;
        }
        consulatAPI = this;
        log = new File(this.getDataFolder(), "log.txt");
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        this.debug = config.getBoolean("debug", false);
        this.development = config.getBoolean("dev", false);
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            nms = (NMS)Class.forName("fr.leconsulat.api.nms.version." + version + ".NMS_" + version).newInstance();
        } catch(InstantiationException | IllegalAccessException | ClassNotFoundException e){
            e.printStackTrace();
        }
        dedicatedServer = ReflectionUtils.getDeclaredField(Bukkit.getServer(), "console");
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        new RedisManager(
                config.getString("redis-host"),
                config.getInt("redis-port"),
                config.getString("redis-password"),
                config.getString("redis-client"));
        protocolManager = ProtocolLibrary.getProtocolManager();
        new SaveManager();
        new ChannelManager();
        new EventManager();
        playerManager = new CPlayerManager();
        commandManager = new CommandManager(this);
        new GuiManager(this);
        registerEvents();
        Bukkit.getScheduler().runTaskTimer(this, new KeepAlive(), 0L, 20 * 60 * 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> this.getServer().getPluginManager().callEvent(new PostInitEvent()), 1L);
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPostInit(PostInitEvent event){
        commandManager.addCommand(new GCCommand());
        commandManager.addCommand(new PermissionCommand());
        commandManager.addCommand(new RankCommand());
        commandManager.addCommand(new OfflineInventoryCommand());
        commandManager.addCommand(new TestCommand());
        if(playerManager.getPlayerClass() == ConsulatPlayer.class && ConsulatAPI.getConsulatAPI().isDebug()){
            for(Player p : Bukkit.getOnlinePlayers()){
                getServer().getPluginManager().callEvent(new PlayerJoinEvent(p, ""));
            }
        }
    }
    
    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(playerManager, this);
    }
    
    @Override
    public void onDisable(){
        for(ConsulatPlayer player : CPlayerManager.getInstance().getConsulatPlayers()){
            player.onQuit();
        }
        RedisManager.getInstance().getRedis().shutdown();
        SaveManager.getInstance().removeAll();
        databaseManager.disconnect();
        
    }
    
    @EventHandler
    public void onEndTick(ServerTickEndEvent event){
        lastTimeTick = (int)event.getTickDuration();
    }
    
    public int getLastTimeTick(){
        return lastTimeTick;
    }
    
    public boolean isDebug(){
        return debug;
    }
    
    public boolean isDevelopment(){
        return development;
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
    
    public Object getDedicatedServer(){
        return dedicatedServer;
    }
    
    public ProtocolManager getProtocolManager(){
        return protocolManager;
    }
    
    public static NMS getNMS(){
        return getConsulatAPI().nms;
    }
}
