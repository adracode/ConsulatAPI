package fr.leconsulat.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import fr.leconsulat.api.channel.ChannelManager;
import fr.leconsulat.api.channel.StaffChannel;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.commands.*;
import fr.leconsulat.api.commands.commands.enchantments.CEnchantCommand;
import fr.leconsulat.api.database.DatabaseManager;
import fr.leconsulat.api.database.SaveManager;
import fr.leconsulat.api.enchantments.EnchantmentManager;
import fr.leconsulat.api.events.ChatSyncEvent;
import fr.leconsulat.api.events.EventManager;
import fr.leconsulat.api.events.PostInitEvent;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.moderation.ModerationDatabase;
import fr.leconsulat.api.nms.api.NMS;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.runnable.KeepAlive;
import fr.leconsulat.api.saver.Saver;
import fr.leconsulat.api.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RTopic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Level;

public class ConsulatAPI extends JavaPlugin implements Listener {
    
    private static ConsulatAPI consulatAPI;
    
    private File playerDataFolder;
    
    private ConsulatServer server;
    private RTopic debugChannel;
    public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
    
    private NMS nms;
    private ProtocolManager protocolManager;
    private CPlayerManager playerManager;
    private DatabaseManager databaseManager;
    private ModerationDatabase moderationDatabase;
    private File log;
    private boolean debug = false;
    private boolean development = false;
    private boolean hasCrashed;
    private boolean chat = true;
    private boolean syncChat = false;
    
    private World theEnd;
    
    private int lastTimeTick = 50;
    
    public int getLastTimeTick(){
        return lastTimeTick;
    }
    
    public boolean isDebug(){
        return debug;
    }
    
    public boolean isChat(){
        return chat;
    }
    
    public boolean isSyncChat(){
        return syncChat;
    }
    
    public void setDebug(boolean debug){
        this.debug = debug;
    }
    
    public boolean isDevelopment(){
        return development;
    }
    
    public void setChat(boolean chat){
        this.chat = chat;
    }
    
    public void setSyncChat(boolean syncChat){
        this.syncChat = syncChat;
        CPlayerManager.getInstance().setSyncChat(syncChat);
        getServer().getPluginManager().callEvent(new ChatSyncEvent(syncChat));
    }
    
    public static ConsulatAPI getConsulatAPI(){
        return consulatAPI;
    }
    
    public static Connection getDatabase(){
        return getConsulatAPI().databaseManager.getDatabase();
    }
    
    public ProtocolManager getProtocolManager(){
        return protocolManager;
    }
    
    public static NMS getNMS(){
        return getConsulatAPI().nms;
    }
    
    public @Nullable World getTheEnd(){
        return theEnd;
    }
    
    @NotNull
    public ConsulatServer getConsulatServer(){
        return server;
    }
    
    public void setServer(ConsulatServer server){
        if(server == ConsulatServer.UNKNOWN){
            this.server = server;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPostInit(PostInitEvent event){
        for(Player p : Bukkit.getOnlinePlayers()){
            getServer().getPluginManager().callEvent(new PlayerJoinEvent(p, ""));
        }
        Saver.getInstance().start();
    }
    
    @EventHandler
    public void onEndTick(ServerTickEndEvent event){
        lastTimeTick = (int)event.getTickDuration();
    }
    
    public void log(Level level, Object message){
        this.getLogger().log(level, message.toString());
    }
    
    public void debug(Level level, Object message){
        if(isDebug()){
            this.getLogger().log(level, message.toString());
        }
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
    
    public void debug(String debugMessage){
        debugChannel.publishAsync(debugMessage);
    }
    
    public File getPlayerFile(UUID uuid){
        return FileUtils.loadFile(playerDataFolder, uuid + ".dat");
    }
    
    public String getPermission(String permission){
        return getName().toLowerCase() + "." + permission;
    }
    
    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(playerManager, this);
    }
    
    public ModerationDatabase getModerationDatabase(){
        return moderationDatabase;
    }
    
    
    @Override
    public void onDisable(){
        for(ConsulatPlayer player : CPlayerManager.getInstance().getConsulatPlayers()){
            player.onQuit();
            player.disconnected();
        }
        Saver.getInstance().end();
        RedisManager.getInstance().getRedis().shutdown();
        SaveManager.getInstance().removeAll();
        databaseManager.disconnect();
        getConfig().set("crashed", false);
        saveConfig();
    }
    
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
        this.server = ConsulatServer.valueOf(
                config.getString("server-name", "unknown").toUpperCase().replaceAll("TEST", ""));
        if(config.get("crashed", null) == null){
            log(Level.WARNING, "No 'crashed' in config, adding.");
            config.set("crashed", false);
            saveConfig();
        }
        this.hasCrashed = config.getBoolean("crashed");
        if(hasCrashed){
            log(Level.SEVERE, "Server has previously crashed !");
        } else {
            log(Level.INFO, "No crash :)");
        }
        config.set("crashed", true);
        saveConfig();
        log(Level.INFO, "Loading in server " + server);
        theEnd = Bukkit.getServer().getAllowEnd() ? Bukkit.getWorlds().get(2) : null;
        playerDataFolder = FileUtils.loadFile(Bukkit.getServer().getWorldContainer(), "world/playerdata/");
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            nms = (NMS)Class.forName("fr.leconsulat.api.nms.version." + version + ".NMS_" + version).newInstance();
        } catch(InstantiationException | IllegalAccessException | ClassNotFoundException e){
            e.printStackTrace();
        }
        new Saver().addSave(() -> SaveManager.getInstance().saveAll());
        databaseManager = new DatabaseManager();
        databaseManager.connect();
        RedisManager redisManager = new RedisManager(
                config.getString("redis-host"),
                config.getInt("redis-port"),
                config.getString("redis-password"),
                config.getString("redis-client"));
        moderationDatabase = new ModerationDatabase();
        debugChannel = redisManager.getRedis().getTopic("Debug");
        protocolManager = ProtocolLibrary.getProtocolManager();
        SaveManager.getInstance();
        EnchantmentManager.getInstance();
        ChannelManager.getInstance();
        EventManager.getInstance();
        playerManager = new CPlayerManager();
        CommandManager.getInstance();
        GuiManager.getInstance();
        new StaffChannel();
        new ADebugCommand().register();
        new AntecedentsCommand().register();
        new CEnchantCommand().register();
        new HelpCommand().register();
        new KickCommand().register();
        new PersoCommand().register();
        new PropertiesCommand().register();
        new RankCommand().register();
        new SanctionCommand().register();
        new SeenCommand().register();
        new StaffChatCommand().register();
        new ToggleChatCommand().register();
        new UnbanCommand().register();
        new UnmuteCommand().register();
        //new OfflineInventoryCommand().register();
        registerEvents();
        Bukkit.getScheduler().runTaskTimer(this, new KeepAlive(), 0L, 20 * 60 * 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> this.getServer().getPluginManager().callEvent(new PostInitEvent()), 1L);
    }
    
    public boolean hasCrashed(){
        return hasCrashed;
    }
}
