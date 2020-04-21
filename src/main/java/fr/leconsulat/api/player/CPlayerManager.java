package fr.leconsulat.api.player;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.events.ConsulatPlayerLeaveEvent;
import fr.leconsulat.api.events.ConsulatPlayerLoadedEvent;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class CPlayerManager implements Listener {
    
    private static CPlayerManager instance;
    
    private Class<?> playerClass;
    private Constructor<?> playerConstructor;
    private Map<UUID, ConsulatPlayer> players = new HashMap<>();
    private Map<String, UUID> offlinePlayers = new HashMap<>();
    
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
    
    private void loadAllPlayers() throws SQLException{
        PreparedStatement load = ConsulatAPI.getDatabase().prepareStatement("SELECT player_uuid, player_name FROM players");
        ResultSet all = load.executeQuery();
        while(all.next()){
            offlinePlayers.put(all.getString("player_name").toLowerCase(),
                    UUID.fromString(all.getString("player_uuid")));
        }
        all.close();
        load.close();
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.setJoinMessage("");
        CPlayerManager manager = CPlayerManager.getInstance();
        ConsulatPlayer player = manager.addPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player " + player + " has joined");
        ConsulatAPI consulatAPI = ConsulatAPI.getConsulatAPI();
        String name = event.getPlayer().getName().toLowerCase();
        if(!offlinePlayers.containsKey(name)){
            offlinePlayers.put(name, event.getPlayer().getUniqueId());
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(consulatAPI, () -> {
            try {
                long start = System.currentTimeMillis();
                ConsulatAPI.getConsulatAPI().log(Level.INFO, "Fetching player...");
                manager.fetchPlayer(player);
                ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player " + player + " fetched in " + (System.currentTimeMillis() - start) + " ms");
                consulatAPI.getServer().getScheduler().scheduleSyncDelayedTask(consulatAPI, () -> {
                    ConsulatAPI.getConsulatAPI().getServer().getPluginManager().callEvent(
                            new ConsulatPlayerLoadedEvent(player)
                    );
                });
            } catch(SQLException e){
                event.getPlayer().kickPlayer("§cErreur lors de la récupération de vos données.\n" + e.getMessage());
                e.printStackTrace();
            }
        });
        
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event){
        event.setQuitMessage("");
        ConsulatAPI.getConsulatAPI().log(Level.INFO, "Player " + event.getPlayer().getName() + " has left");
        Bukkit.getServer().getPluginManager().callEvent(new ConsulatPlayerLeaveEvent(getConsulatPlayer(event.getPlayer().getUniqueId())));
        this.players.remove(event.getPlayer().getUniqueId());
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
            players.put(uuid, player);
        }
        return player;
    }
    
    public void fetchPlayer(ConsulatPlayer player) throws SQLException{
        PreparedStatement fetch = ConsulatAPI.getDatabase().prepareStatement("SELECT id, player_rank, registered, buyedPerso, prefix_perso FROM players WHERE player_uuid = ?");
        fetch.setString(1, player.getUUID().toString());
        ResultSet resultSet = fetch.executeQuery();
        if(resultSet.next()){
            player.initialize(
                    resultSet.getInt("id"),
                    Rank.byName(resultSet.getString("player_rank")),
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
    
    public Optional<ConsulatOffline> fetchOffline(String playerName) throws SQLException{
        PreparedStatement fetch = ConsulatAPI.getDatabase().prepareStatement("SELECT * FROM players WHERE player_name = ?");
        fetch.setString(1, playerName);
        ResultSet resultFetch = fetch.executeQuery();
        ConsulatOffline offline;
        if(resultFetch.next()){
            offline = new ConsulatOffline(
                    resultFetch.getInt("id"),
                    UUID.fromString(resultFetch.getString("player_uuid")),
                    resultFetch.getString("player_name"),
                    Rank.byName(resultFetch.getString("player_rank")),
                    resultFetch.getString("registered"));
        } else {
            return Optional.empty();
        }
        resultFetch.close();
        fetch.close();
        PreparedStatement lastConnection = ConsulatAPI.getDatabase().prepareStatement(
                "SELECT connection_date FROM connections WHERE player_id = ? ORDER BY id DESC LIMIT 1");
        lastConnection.setInt(1, offline.getId());
        ResultSet resultLastConnection = lastConnection.executeQuery();
        if(resultLastConnection.next()){
            offline.setLastConnection(resultLastConnection.getString("connection_date"));
        }
        resultLastConnection.close();
        lastConnection.close();
        return Optional.of(offline);
    }
    
    public void setRank(UUID uuid, Rank rank) throws SQLException{
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET player_rank = ? WHERE player_uuid = ?");
        request.setString(1, rank.getRankName());
        request.setString(2, uuid.toString());
        request.executeUpdate();
        request.close();
    }
    
    public void setHasCustomRank(UUID uuid, boolean hasCustomRank) throws SQLException{
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET buyedPerso = ? WHERE player_uuid = ?");
        request.setBoolean(1, hasCustomRank);
        request.setString(2, uuid.toString());
        request.executeUpdate();
        request.close();
    }
    
    public static CPlayerManager getInstance(){
        return instance;
    }
    
    public void setCustomRank(UUID uuid, String rank) throws SQLException{
        PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET prefix_perso = ? WHERE player_uuid = ?");
        request.setString(1, rank);
        request.setString(2, uuid.toString());
        request.executeUpdate();
        request.close();
    }
}