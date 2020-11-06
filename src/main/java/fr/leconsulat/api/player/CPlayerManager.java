package fr.leconsulat.api.player;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.ConsulatServer;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.channel.ChannelManager;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.commands.ADebugCommand;
import fr.leconsulat.api.events.ConsulatPlayerLeaveEvent;
import fr.leconsulat.api.events.ConsulatPlayerLoadedEvent;
import fr.leconsulat.api.moderation.BanReason;
import fr.leconsulat.api.moderation.MuteReason;
import fr.leconsulat.api.moderation.SanctionType;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.player.stream.OfflinePlayerInputStream;
import fr.leconsulat.api.player.stream.PlayerInputStream;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
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
import org.redisson.api.RTopic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    
    private final File playerDataFolder = FileUtils.loadFile(Bukkit.getServer().getWorldContainer(), "world/playerdata/");
    private final Map<UUID, ConsulatPlayer> players = new HashMap<>();
    private final Map<String, UUID> offlinePlayers = new HashMap<>();
    private final Map<UUID, byte[]> pendingPlayer = new HashMap<>();
    //Fait une action avec l'ancien serveur
    private BiConsumer<ConsulatPlayer, ConsulatServer> onJoin;
    private Function<ConsulatPlayer, Set<String>> rankPermission;
    private Class<?> playerClass;
    private Constructor<?> playerConstructor;
    private RTopic syncChat;
    private int syncChatListener;
    
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
    
    public void setSyncChat(boolean syncChat){
        RedisManager redis = RedisManager.getInstance();
        String channel = (ConsulatAPI.getConsulatAPI().isDevelopment() ? "Dev" : "") + "Chat";
        if(syncChat){
            this.syncChat = redis.getRedis().getTopic(channel);
            this.syncChatListener = redis.register(channel, String.class, (chan, message) -> {
                int separator = message.indexOf(':');
                ConsulatServer server = ConsulatServer.valueOf(message.substring(0, separator));
                ConsulatServer currentServer = ConsulatAPI.getConsulatAPI().getConsulatServer();
                Bukkit.broadcastMessage(
                        (currentServer != server ? "§2[" + server.getDisplay() + "] " : "")
                        + message.substring(separator + 1));
            });
        } else {
            this.syncChat = null;
            redis.getRedis().getTopic(channel).removeListener(this.syncChatListener);
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
                    new PlayerInputStream(player.getPlayer(), data).readFully().close();
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
            CompoundTag playerTag = is.read();
            is.close();
            float saveHealth = playerTag.getFloat("Health");
            int saveFood = playerTag.getInt("foodLevel");
            float saveSaturation = playerTag.getFloat("foodSaturationLevel");
            float saveExhaustion = playerTag.getFloat("foodExhaustionLevel");
            int saveFoodTimer = playerTag.getInt("foodTickTimer");
            float saveXp = playerTag.getInt("XpLevel") + playerTag.getFloat("XpP");
            ListTag<CompoundTag> saveInventory = playerTag.getListTag("Inventory", NBTType.COMPOUND);
            ListTag<CompoundTag> saveEffects = playerTag.getListTag("ActiveEffects", NBTType.COMPOUND);
            playerTag.put("ActiveEffects", inputStream.fetchActiveEffects());
            playerTag.putFloat("Health", inputStream.fetchHealth());
            playerTag.putInt("foodLevel", inputStream.fetchFood());
            playerTag.putFloat("foodSaturationLevel", inputStream.fetchSaturation());
            playerTag.putFloat("foodExhaustionLevel", inputStream.fetchExhaustion());
            playerTag.putInt("foodTickTimer", inputStream.fetchFoodTickTimer());
            float experience = inputStream.fetchLevel();
            int level = (int)experience;
            playerTag.putInt("XpLevel", level);
            playerTag.putFloat("XpP", experience - level);
            playerTag.put("Inventory", inputStream.fetchInventory());
            NBTOutputStream os = new NBTOutputStream(playerFile, playerTag);
            try {
                os.write("");
            } catch(Exception e){
                ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(uuid);
                if(player != null){
                    player.addPermission(ConsulatPlayer.ERROR);
                    Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                        player.getPlayer().kickPlayer("§7§l§m ----[ §r§6§lLe Consulat §7§l§m]----\n\n§cUne erreur critique est survenue. Contacte un admin / développeur sur Discord.\n");
                    });
                } else {
                    ConsulatPlayer.addPermission(uuid, ConsulatPlayer.ERROR);
                }
                playerTag.putFloat("Health", saveHealth);
                playerTag.putInt("foodLevel", saveFood);
                playerTag.putFloat("foodSaturationLevel", saveSaturation);
                playerTag.putFloat("foodExhaustionLevel", saveExhaustion);
                playerTag.putInt("foodTickTimer", saveFoodTimer);
                playerTag.putInt("XpLevel", (int)saveXp);
                playerTag.putFloat("XpP", saveXp - (int)saveXp);
                playerTag.put("Inventory", saveInventory);
                playerTag.put("ActiveEffects", saveEffects);
                os = new NBTOutputStream(playerFile, playerTag);
                os.write("");
            } finally {
                os.close();
            }
        } catch(IOException | NullPointerException e){
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        ConsulatAPI api = ConsulatAPI.getConsulatAPI();
        event.setJoinMessage("");
        ConsulatPlayer player = addPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        RFuture<String> server = player.getServer();
        byte[] loadData = pendingPlayer.remove(player.getUUID());
        if(loadData != null){
            new PlayerInputStream(player.getPlayer(), loadData).readFully().close();
            player.setInventoryBlocked(false);
        }
        server.thenRun(player::setServer);
        server.onComplete((oldServer, exception) -> {
            ConsulatServer consulatServer = oldServer == null ? ConsulatServer.UNKNOWN : ConsulatServer.valueOf(oldServer);
            if(onJoin != null){
                onJoin.accept(player, consulatServer);
            }
            player.setServer();
        });
        if(api.isDebug()){
            api.log(Level.INFO, "Player " + player + " has joined");
        }
        if(api.isDevelopment()){
            player.getPlayer().sendTitle("§4Serveur", "§4en développement", 20, 100, 20);
            player.getPlayer().sendMessage("§cTu es sur un serveur en développement ! Des bugs peuvent être présents.");
        }
        String name = event.getPlayer().getName().toLowerCase();
        if(!offlinePlayers.containsKey(name)){
            offlinePlayers.put(name, event.getPlayer().getUniqueId());
        }
        float health = (float)player.getPlayer().getHealth();
        Bukkit.getScheduler().runTaskLater(ConsulatAPI.getConsulatAPI(), () -> player.getPlayer().setHealth(health), 1L);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(api, () -> {
            try {
                long start = System.currentTimeMillis();
                fetchPlayer(player);
                setAntecedents(player);
                ConsulatAPI.getConsulatAPI().getModerationDatabase().setMute(player.getPlayer());
                api.log(Level.INFO, "Player " + (api.isDebug() ? player : player.getName()) + " fetched in " + (System.currentTimeMillis() - start) + " ms");
                start = System.currentTimeMillis();
                player.load();
                if(player.isError()){
                    api.log(Level.WARNING, "Player " + (api.isDebug() ? player : player.getName()) + " errored !");
                    Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                        player.getPlayer().kickPlayer("§7§l§m ----[ §r§6§lLe Consulat §7§l§m]----\n\n§cUne erreur critique est survenue. Contacte un admin / développeur sur Discord.\n");
                    });
                    return;
                }
                if(api.isDebug()){
                    api.log(Level.INFO, "Getting permissions in " + (System.currentTimeMillis() - start) + " ms");
                }
                Bukkit.getScheduler().runTask(api, () -> Bukkit.getServer().getPluginManager().callEvent(new ConsulatPlayerLoadedEvent(player)));
            } catch(SQLException e){
                Bukkit.getScheduler().runTask(api, () -> event.getPlayer().kickPlayer("§cErreur lors de la récupération de vos données.\n"));
                e.printStackTrace();
            }
        });
    }
    
    @EventHandler
    public void onConsulatPlayerLoaded(ConsulatPlayerLoadedEvent event){
        ConsulatPlayer player = event.getPlayer();
        CommandManager.getInstance().sendCommands(player);
        if(ADebugCommand.UUID_PERMISSION.contains(player.getUUID())){
            player.addPermission(CommandManager.getInstance().getCommand("adebug").getPermission());
        }
        if(player.hasPermission(CommandManager.getInstance().getCommand("staffchat").getPermission())){
            ChannelManager.getInstance().getChannel("staff").addPlayer(player);
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
                if(serverStr == null || ConsulatServer.valueOf(serverStr) == ConsulatAPI.getConsulatAPI().getConsulatServer() && Bukkit.getPlayer(player.getUUID()) == null){
                    player.disconnected();
                }
            });
        }, 20);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        ConsulatPlayer player = CPlayerManager.getInstance().getConsulatPlayer(event.getPlayer().getUniqueId());
        if(player == null){
            event.getPlayer().sendMessage(Text.ERROR);
            event.setCancelled(true);
            return;
        }
        String message = player.chat(event.getMessage());
        if(message == null){
            event.setCancelled(true);
            return;
        }
        ConsulatAPI api = ConsulatAPI.getConsulatAPI();
        if(api.isSyncChat()){
            syncChat.publishAsync(api.getConsulatServer().name() + ":" + player.getDisplayName() + "§7: §f" + message);
            event.setCancelled(true);
        } else {
            event.setMessage(message);
            event.setFormat(player.getDisplayRank() + " %s§7: §f%s");
        }
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
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEnterEnd(PlayerPortalEvent event){
        if(event.getTo() == null){
            return;
        }
        if(Objects.equals(ConsulatAPI.getConsulatAPI().getTheEnd(), event.getTo().getWorld()) && !Bukkit.getServer().getAllowEnd()){
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
        PreparedStatement fetch = ConsulatAPI.getDatabase().prepareStatement("SELECT id, player_rank, registered, buyedPerso, prefix_perso, public_api FROM players WHERE player_uuid = ?");
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
                    resultSet.getString("registered"),
                    resultSet.getBoolean("public_api")
            );
        } else {
            player.initialize(0, Rank.INVITE, false, null, null, false);
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
    
    public void setHasCustomRank(UUID uuid, boolean hasCustomRank){
        try {
            PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET buyedPerso = ? WHERE player_uuid = ?");
            request.setBoolean(1, hasCustomRank);
            request.setString(2, uuid.toString());
            request.executeUpdate();
            request.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void setApi(ConsulatPlayer player, boolean api){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                PreparedStatement setApi = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET public_api = ? WHERE player_uuid = ?");
                setApi.setBoolean(1, api);
                setApi.setString(2, player.getUUID().toString());
                setApi.executeUpdate();
                setApi.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        });
    }
    
    private void setAntecedents(ConsulatPlayer player) throws SQLException{
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT sanction, reason FROM antecedents WHERE playeruuid = ? AND cancelled = 0");
        preparedStatement.setString(1, player.getUUID().toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while(resultSet.next()){
            SanctionType sanctionType = SanctionType.valueOf(resultSet.getString("sanction"));
            String reason = resultSet.getString("reason");
            if(sanctionType == SanctionType.MUTE){
                MuteReason muteReason = Arrays.stream(MuteReason.values()).filter(mute -> mute.getSanctionName().equals(reason)).findFirst().orElse(null);
                if(muteReason != null){
                    if(player.getMuteHistory().containsKey(muteReason)){
                        int number = player.getMuteHistory().get(muteReason);
                        player.getMuteHistory().put(muteReason, ++number);
                    } else {
                        player.getMuteHistory().put(muteReason, 1);
                    }
                }
            } else {
                BanReason banReason = Arrays.stream(BanReason.values()).filter(ban -> ban.getSanctionName().equals(reason)).findFirst().orElse(null);
                if(banReason != null){
                    if(player.getBanHistory().containsKey(banReason)){
                        int number = player.getBanHistory().get(banReason);
                        player.getBanHistory().put(banReason, ++number);
                    } else {
                        player.getBanHistory().put(banReason, 1);
                    }
                }
            }
        }
    }
    
    public void setCustomRank(UUID uuid, String rank){
        try {
            PreparedStatement request = ConsulatAPI.getDatabase().prepareStatement("UPDATE players SET prefix_perso = ? WHERE player_uuid = ?");
            request.setString(1, rank);
            request.setString(2, uuid.toString());
            request.executeUpdate();
            request.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public Set<String> getDefaultPermissions(ConsulatPlayer player){
        Set<String> permissions = rankPermission == null ? new HashSet<>() : rankPermission.apply(player);
        CommandManager commandManager = CommandManager.getInstance();
        if(ADebugCommand.UUID_PERMISSION.contains(player.getUUID())){
            permissions.add(commandManager.getCommand("adebug").getPermission());
        }
        switch(player.getRank()){
            case ADMIN:
            case RESPONSABLE:
                permissions.add(ConsulatAPI.getConsulatAPI().getPermission("bypass-commands"));
                break;
                /*permissions.add("minecraft.commands");
                permissions.add("bukkit.commands");
                for(Plugin plugin : Bukkit.getPluginManager().getPlugins()){
                    permissions.add(plugin.getName().toLowerCase() + ".commands");
                }
                break;*/
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