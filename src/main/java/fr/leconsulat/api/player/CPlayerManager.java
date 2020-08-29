package fr.leconsulat.api.player;

import com.comphenix.protocol.utility.MinecraftReflection;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.ConsulatServer;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.commands.ADebugCommand;
import fr.leconsulat.api.events.ConsulatPlayerLeaveEvent;
import fr.leconsulat.api.events.ConsulatPlayerLoadedEvent;
import fr.leconsulat.api.nbt.CompoundTag;
import fr.leconsulat.api.nbt.NBTInputStream;
import fr.leconsulat.api.nbt.NBTOutputStream;
import fr.leconsulat.api.player.stream.OfflinePlayerInputStream;
import fr.leconsulat.api.player.stream.PlayerInputStream;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.utils.FileUtils;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.redisson.api.RFuture;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public class CPlayerManager implements Listener {
    
    private static CPlayerManager instance;
    private static Method getEntity;
    
    static{
        try {
            getEntity = MinecraftReflection.getMinecraftClass("CommandListenerWrapper").getMethod("h");
        } catch(NoSuchMethodException var1){
            var1.printStackTrace();
        }
    }
    
    private final File playerDataFolder = FileUtils.loadFile(Bukkit.getServer().getWorldContainer(), "world/playerdata/");
    private final Map<UUID, ConsulatPlayer> players = new HashMap<>();
    private final Map<String, UUID> offlinePlayers = new HashMap<>();
    private final Map<UUID, byte[]> pendingPlayer = new HashMap<>();
    //Fait une action avec l'ancien serveur
    private BiConsumer<ConsulatPlayer, ConsulatServer> onJoin;
    private Function<ConsulatPlayer, Set<String>> rankPermission;
    private Class<?> playerClass;
    private Constructor<?> playerConstructor;
    public CPlayerManager(){
        if(instance != null){
            return;
        }
        instance = this;
        try {
            playerClass = Class.forName(ConsulatAPI.getConsulatAPI().getConfig().getString("player"));
            ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player class is " + playerClass.getName());
        } catch(ClassNotFoundException e){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "La classe du ConsulatPlayer est invalide. Chargement du ConsulatPlayer par défaut.");
            playerClass = ConsulatPlayer.class;
        }
        if(!ReflectionUtils.isSuper(ConsulatPlayer.class, playerClass)){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, playerClass + " n'hérite pas de ConsulatPlayer. Chargement du ConsulatPlayer par défaut.");
            playerClass = ConsulatPlayer.class;
        }
        try {
            playerConstructor = playerClass.getDeclaredConstructor(UUID.class, String.class);
            ConsulatAPI.getConsulatAPI().log(Level.INFO, "Loaded player constructor");
        } catch(NoSuchMethodException e){
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, playerClass + " ne possède pas de constructeur adapté.");
            e.printStackTrace();
        }
        //Volontairement bloquant
        try {
            long start = System.currentTimeMillis();
            loadAllPlayers();
            ConsulatAPI.getConsulatAPI().log(Level.INFO, "Loaded offlines players in " + (System.currentTimeMillis() - start) + " ms");
        } catch(SQLException e){
            e.printStackTrace();
            ConsulatAPI.getConsulatAPI().log(Level.SEVERE, "Error while loading offline players");
            Bukkit.shutdown();
        }
    }
    
    /**
     * Renvoie tous les joueurs connectés sous forme de ConsulatPlayer
     *
     * @return Une collection contenant  les joueurs
     */
    public Collection<ConsulatPlayer> getConsulatPlayers(){
        return Collections.unmodifiableCollection(players.values());
    }
    
    public static CPlayerManager getInstance(){
        return instance;
    }
    
    public Class<?> getPlayerClass(){
        return playerClass;
    }
    
    public void setRankPermission(Function<ConsulatPlayer, Set<String>> rankPermission){
        this.rankPermission = rankPermission;
    }
    
    public void loadPlayerData(byte[] data){
        OfflinePlayerInputStream input = new OfflinePlayerInputStream(data);
        UUID uuid = input.fetchUUID();
        synchronized(players){
            ConsulatPlayer player = players.get(uuid);
            if(player != null){
                if(player.isInventoryBlocked()){
                    new PlayerInputStream(player.getPlayer(), data).readLevel().readInventory().close();
                    player.setInventoryBlocked(false);
                    player.sendMessage("§7Inventaire chargé.");
                }
            } else {
                pendingPlayer.put(uuid, data);
            }
            input.close();
        }
    }
    
    public void savePlayerData(byte[] data){
        try {
            OfflinePlayerInputStream inputStream = new OfflinePlayerInputStream(data);
            UUID uuid = inputStream.fetchUUID();
            File playerFile = getPlayerFile(uuid);
            NBTInputStream is = new NBTInputStream(playerFile);
            CompoundTag player = is.read();
            is.close();
            float experience = inputStream.fetchLevel();
            int level = (int)experience;
            player.putInt("XpLevel", level);
            player.putFloat("XpP", experience - level);
            player.put("Inventory", inputStream.fetchInventory());
            NBTOutputStream os = new NBTOutputStream(playerFile, player);
            os.write("");
            os.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.setJoinMessage("");
        ConsulatPlayer player = addPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        RFuture<String> server = player.getServer();
        byte[] loadData = pendingPlayer.remove(player.getUUID());
        if(loadData != null){
            new PlayerInputStream(player.getPlayer(), loadData).readLevel().readInventory().close();
            player.setInventoryBlocked(false);
        }
        server.thenRun(player::setServer);
        if(onJoin != null){
            server.onComplete((oldServer, exception) -> {
                ConsulatServer consulatServer = oldServer == null ? ConsulatServer.UNKNOWN : ConsulatServer.valueOf(oldServer);
                onJoin.accept(player, consulatServer);
                player.setServer();
            });
        }
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player " + player + " has joined");
        if(ConsulatAPI.getConsulatAPI().isDevelopment()){
            player.getPlayer().sendTitle("§4Serveur", "§4en développement", 20, 100, 20);
            player.getPlayer().sendMessage("§cTu es sur un serveur en développement ! Des bugs peuvent être présents.");
        }
        ConsulatAPI consulatAPI = ConsulatAPI.getConsulatAPI();
        String name = event.getPlayer().getName().toLowerCase();
        if(!offlinePlayers.containsKey(name)){
            offlinePlayers.put(name, event.getPlayer().getUniqueId());
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(consulatAPI, () -> {
            try {
                ConsulatAPI.getConsulatAPI().log(Level.INFO, "Fetching player...");
                long start = System.currentTimeMillis();
                fetchPlayer(player);
                ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player " + player + " fetched in " + (System.currentTimeMillis() - start) + " ms");
                ConsulatAPI.getConsulatAPI().log(Level.INFO, "Getting permissions...");
                start = System.currentTimeMillis();
                player.load();
                ConsulatAPI.getConsulatAPI().log(Level.INFO, "Getting permissions in " + (System.currentTimeMillis() - start) + " ms");
                Bukkit.getScheduler().runTask(consulatAPI,
                        () -> Bukkit.getServer().getPluginManager().callEvent(new ConsulatPlayerLoadedEvent(player)));
            } catch(SQLException e){
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () ->
                        event.getPlayer().kickPlayer("§cErreur lors de la récupération de vos données.\n" + e.getMessage()));
                e.printStackTrace();
            }
        });
    }
    
    @EventHandler
    public void onConsulatPlayerLoaded(ConsulatPlayerLoadedEvent event){
        CommandManager.getInstance().sendCommands(event.getPlayer());
        if(ADebugCommand.UUID_PERMISSION.contains(event.getPlayer().getUUID())){
            event.getPlayer().addPermission(CommandManager.getInstance().getCommand("adebug").getPermission());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event){
        event.setQuitMessage("");
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player " + event.getPlayer().getName() + " has left");
        Bukkit.getServer().getPluginManager().callEvent(new ConsulatPlayerLeaveEvent(getConsulatPlayer(event.getPlayer().getUniqueId())));
        this.players.remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onQuit(ConsulatPlayerLeaveEvent event){
        ConsulatPlayer player = event.getPlayer();
        player.onQuit();
        Bukkit.getScheduler().scheduleSyncDelayedTask(ConsulatAPI.getConsulatAPI(), () -> {
            player.getServer().onComplete((serverStr, exception) -> {
                ConsulatServer server = ConsulatServer.valueOf(serverStr);
                if(server == ConsulatAPI.getConsulatAPI().getConsulatServer() && Bukkit.getPlayer(player.getUUID()) == null){
                    player.disconnected();
                }
            });
        }, 20);
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockInventory(InventoryClickEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getWhoClicked().getUniqueId());
        if(player != null && player.isInventoryBlocked()){
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockInventory(PlayerDropItemEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player != null && player.isInventoryBlocked()){
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockInventory(PlayerInteractEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player != null && player.isInventoryBlocked()){
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockInventory(ProjectileLaunchEvent event){
        if(event.getEntity().getShooter() instanceof Player){
            ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(((Player)event.getEntity().getShooter()).getUniqueId());
            if(player != null && player.isInventoryBlocked()){
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockInventory(PlayerItemConsumeEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player != null && player.isInventoryBlocked()){
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockInventory(PlayerInteractAtEntityEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player != null && player.isInventoryBlocked()){
            event.setCancelled(true);
        }
    }
    
    public void onJoin(BiConsumer<ConsulatPlayer, ConsulatServer> onJoin){
        this.onJoin = onJoin;
    }
    
    public ConsulatPlayer getConsulatPlayer(UUID uuid){
        return players.get(uuid);
    }
    
    public ConsulatPlayer getConsulatPlayer(String name){
        UUID uuid = getPlayerUUID(name);
        if(uuid == null){
            return null;
        }
        return getConsulatPlayer(uuid);
    }
    
    public UUID getPlayerUUID(String name){
        return offlinePlayers.get(name.toLowerCase());
    }
    
    public ConsulatPlayer addPlayer(UUID uuid, String name){
        ConsulatPlayer player = getConsulatPlayer(uuid);
        if(player == null){
            try {
                player = (ConsulatPlayer)playerConstructor.newInstance(uuid, name);
            } catch(InstantiationException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
            synchronized(players){
                players.put(uuid, player);
            }
        }
        return player;
    }
    
    public void fetchPlayer(ConsulatPlayer player) throws SQLException{
        PreparedStatement fetch = ConsulatAPI.getDatabase().prepareStatement("SELECT id, player_rank, registered, buyedPerso, prefix_perso FROM players WHERE player_uuid = ?");
        fetch.setString(1, player.getUUID().toString());
        ResultSet resultSet = fetch.executeQuery();
        if(resultSet.next()){
            int id = resultSet.getInt("id");
            String rank = resultSet.getString("player_rank");
            if(rank == null){
                resultSet.close();
                fetch.close();
                throw new SQLException("player_rank is null at id " + id);
            }
            player.initialize(
                    id,
                    Rank.byName(rank),
                    resultSet.getBoolean("buyedPerso"),
                    resultSet.getString("prefix_perso"),
                    resultSet.getString("registered")
            );
        } else {
            player.initialize(0, Rank.INVITE, false, null, null);
        }
        resultSet.close();
        fetch.close();
    }
    
    public void fetchOffline(String playerName, Consumer<ConsulatOffline> consumer){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                PreparedStatement fetch = ConsulatAPI.getDatabase().prepareStatement(
                        "SELECT * FROM players WHERE player_name = ?");
                fetch.setString(1, playerName);
                ResultSet resultFetch = fetch.executeQuery();
                final ConsulatOffline consulatOffline;
                if(resultFetch.next()){
                    int id = resultFetch.getInt("id");
                    String uuid = resultFetch.getString("player_uuid");
                    if(uuid == null){
                        resultFetch.close();
                        fetch.close();
                        throw new SQLException("player_uuid is null at id " + id);
                    }
                    String rank = resultFetch.getString("player_rank");
                    if(rank == null){
                        resultFetch.close();
                        fetch.close();
                        throw new SQLException("player_rank is null at id " + id);
                    }
                    consulatOffline = new ConsulatOffline(
                            id,
                            UUID.fromString(uuid),
                            resultFetch.getString("player_name"),
                            Rank.byName(rank),
                            resultFetch.getString("registered"));
                } else {
                    Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                        consumer.accept(null);
                    });
                    return;
                }
                resultFetch.close();
                fetch.close();
                PreparedStatement lastConnection = ConsulatAPI.getDatabase().prepareStatement(
                        "SELECT connection_date FROM connections WHERE player_id = ? ORDER BY id DESC LIMIT 1");
                lastConnection.setInt(1, consulatOffline.getId());
                ResultSet resultLastConnection = lastConnection.executeQuery();
                if(resultLastConnection.next()){
                    consulatOffline.setLastConnection(resultLastConnection.getString("connection_date"));
                }
                resultLastConnection.close();
                lastConnection.close();
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                    consumer.accept(consulatOffline);
                });
            } catch(SQLException e){
                e.printStackTrace();
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                    consumer.accept(null);
                });
            }
        });
    }
    
    public void setRank(UUID uuid, Rank rank){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET player_rank = ? WHERE player_uuid = ?");
                request.setString(1, rank.getRankName());
                request.setString(2, uuid.toString());
                request.executeUpdate();
                request.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        });
    }
    
    public void setHasCustomRank(UUID uuid, boolean hasCustomRank) throws SQLException{
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET buyedPerso = ? WHERE player_uuid = ?");
        request.setBoolean(1, hasCustomRank);
        request.setString(2, uuid.toString());
        request.executeUpdate();
        request.close();
    }
    
    public void setCustomRank(UUID uuid, String rank) throws SQLException{
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET prefix_perso = ? WHERE player_uuid = ?");
        request.setString(1, rank);
        request.setString(2, uuid.toString());
        request.executeUpdate();
        request.close();
    }
    
    public Set<String> getDefaultPermissions(ConsulatPlayer player){
        Set<String> permissions = rankPermission == null ? new HashSet<>() : rankPermission.apply(player);
        CommandManager commandManager = CommandManager.getInstance();
        if(ADebugCommand.UUID_PERMISSION.contains(player.getUUID())){
            permissions.add(commandManager.getCommand("adebug").getPermission());
        }
        switch(player.getRank()){
            case RESPONSABLE:
            case ADMIN:
                permissions.add(ConsulatAPI.getConsulatAPI().getPermission("allow-other-plugin-commands"));
                break;
        }
        return permissions;
    }
    
    private File getPlayerFile(UUID uuid){
        return FileUtils.loadFile(playerDataFolder, uuid + ".dat");
    }
    
    private void loadAllPlayers() throws SQLException{
        PreparedStatement load = ConsulatAPI.getDatabase().prepareStatement("SELECT id, player_uuid, player_name FROM players");
        ResultSet all = load.executeQuery();
        while(all.next()){
            String name = all.getString("player_name");
            if(name == null){
                ConsulatAPI.getConsulatAPI().log(Level.WARNING, "NULL player_name at id " + all.getInt("id"));
                continue;
            }
            String uuid = all.getString("player_uuid");
            if(uuid == null){
                ConsulatAPI.getConsulatAPI().log(Level.WARNING, "NULL player_uuid at id " + all.getInt("id"));
                continue;
            }
            offlinePlayers.put(name.toLowerCase(), UUID.fromString(uuid));
        }
        all.close();
        load.close();
    }
    
    public static String getRedisKey(UUID uuid){
        return "player:" + uuid;
    }
}