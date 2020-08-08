package fr.leconsulat.api.player;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.ConsulatServer;
import fr.leconsulat.api.channel.Channel;
import fr.leconsulat.api.database.Saveable;
import fr.leconsulat.api.events.PlayerChangeRankEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.utils.FileUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings({"unused"})
public class ConsulatPlayer implements Saveable {
    
    private int id;
    private final UUID uuid;
    private final Player player;
    private Set<String> permissions = new HashSet<>();
    private final String name;
    private Rank rank;
    private boolean initialized = false;
    private CustomRank customRank;
    private String registered;
    private IGui currentlyOpen;
    private Channel currentChannel = null;
    private boolean vanished;
    private int positionInQueue = 0;
    private boolean disconnectHandled = false;
    private boolean inventoryBlocked = true;
    
    public ConsulatPlayer(UUID uuid, String name){
        if(uuid == null || name == null){
            throw new NullPointerException("Consulat Player cannot be instantiated with null arguments");
        }
        this.uuid = uuid;
        this.name = name;
        this.player = Bukkit.getPlayer(uuid);
        if(player == null){
            throw new NullPointerException("Player cannot be null");
        }
        player.setCanPickupItems(false);
    }
    
    public void initialize(int id, Rank rank, boolean hasCustomRank, String customRank, String registered){
        this.id = id;
        this.rank = rank;
        this.customRank = hasCustomRank ? new CustomRank(customRank) : null;
        this.registered = registered;
        this.initialized = true;
    }
    
    public void onQuit(){
        save();
    }
    
    public int getId(){
        return id;
    }
    
    public UUID getUUID(){
        return uuid;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public Rank getRank(){
        return rank;
    }
    
    public void setRank(Rank rank){
        PlayerChangeRankEvent event = new PlayerChangeRankEvent(this, rank);
        Bukkit.getPluginManager().callEvent(event);
        CPlayerManager.getInstance().setRank(getUUID(), rank);
        this.rank = rank;
    }
    
    public boolean isInitialized(){
        return initialized;
    }
    
    public boolean hasCustomRank(){
        return customRank != null;
    }
    
    public void setHasCustomRank(boolean hasCustomRank) throws SQLException{
        CPlayerManager.getInstance().setHasCustomRank(getUUID(), hasCustomRank);
        if(hasCustomRank){
            customRank = new CustomRank();
        } else {
            customRank = null;
        }
    }
    
    public void setColorPrefix(ChatColor colorPrefix){
        if(!hasCustomRank()){
            return;
        }
        this.customRank.setColorPrefix(colorPrefix);
    }
    
    public void setPrefix(String prefix){
        if(!hasCustomRank()){
            return;
        }
        this.customRank.setPrefix(prefix);
    }
    
    public void setColorName(ChatColor colorName){
        if(!hasCustomRank()){
            return;
        }
        this.customRank.setColorName(colorName);
    }
    
    public void resetCustomRank() throws SQLException{
        if(!hasCustomRank()){
            return;
        }
        CPlayerManager.getInstance().setCustomRank(getUUID(), null);
        this.customRank.reset();
    }
    
    public void applyCustomRank() throws SQLException{
        if(!hasCustomRank()){
            return;
        }
        CPlayerManager.getInstance().setCustomRank(getUUID(), customRank.getCustomRank().replace('§', '&'));
    }
    
    public String getCustomRank(){
        return hasCustomRank() ? customRank.getCustomRank() : null;
    }
    
    public String getCustomPrefix(){
        return hasCustomRank() ? customRank.getCustomPrefix() : null;
    }
    
    public boolean hasPower(Rank neededRank){
        return this.rank != null && this.rank.getRankPower() >= neededRank.getRankPower();
    }
    
    public String getName(){
        return name;
    }
    
    public String getRegistered(){
        return registered;
    }
    
    public void sendMessage(String message){
        getPlayer().sendMessage(message);
    }
    
    public void sendMessage(TextComponent... message){
        getPlayer().spigot().sendMessage(message);
    }
    
    public boolean isVanished(){
        return vanished;
    }
    
    public void setVanished(boolean vanished){
        this.vanished = vanished;
    }
    
    public boolean hasPermission(Permission permission){
        return hasPermission(permission.getPermission());
    }
    
    public boolean hasPermission(String permission){
        return permissions.contains(permission);
    }
    
    public void addPermission(Permission... permissions){
        for(Permission permission : permissions){
            addPermission(permission.getPermission());
        }
    }
    
    public void addPermission(String... permissions){
        for(String permission : permissions){
            addPermission(permission);
        }
    }
    
    public void addPermission(String permission){
        this.permissions.add(permission);
    }
    
    public void removePermission(String permission){
        this.permissions.remove(permission);
    }
    
    //TODO
    private Set<String> getRankPermissions(){
        Set<String> permissions = new HashSet<>();
        switch(rank){
            case INVITE:
                return Collections.emptySet();
            case JOUEUR:
            case ADMIN:
                permissions.addAll(
                        Collections.singletonList("consulat.api.test")
                );
                break;
        }
        return permissions;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof ConsulatPlayer)){
            return false;
        }
        return uuid.equals(((ConsulatPlayer)o).uuid);
    }
    
    @Override
    public int hashCode(){
        return uuid.hashCode();
    }
    
    @Override
    public String toString(){
        return "ConsulatPlayer{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", player=" + player +
                ", permissions=" + permissions +
                ", name='" + name + '\'' +
                ", rank=" + rank +
                ", initialized=" + initialized +
                ", customRank=" + customRank +
                ", registered='" + registered + '\'' +
                ", currentlyOpen=" + currentlyOpen +
                ", currentChannel=" + currentChannel +
                ", vanished=" + vanished +
                '}';
    }
    
    public IGui getCurrentlyOpen(){
        return currentlyOpen;
    }
    
    public void setCurrentlyOpen(IGui gui){
        this.currentlyOpen = gui;
    }
    
    public Channel getCurrentChannel(){
        return currentChannel;
    }
    
    public void setCurrentChannel(Channel currentChannel){
        this.currentChannel = currentChannel;
    }
    
    public String getDisplayName(){
        String customRank = getCustomRank();
        return customRank == null ? rank.getRankColor() + "[" + rank.getRankName() + "]" : customRank;
    }
    
    public boolean isInQueue(){
        return positionInQueue > 0;
    }
    
    public int getPositionInQueue(){
        return positionInQueue;
    }
    
    public void setPositionInQueue(int positionInQueue){
        this.positionInQueue = positionInQueue;
    }
    
    public int decrementPosition(){
        return --positionInQueue;
    }
    
    public boolean isDisconnectHandled(){
        return disconnectHandled;
    }
    
    public void setDisconnectHandled(boolean disconnectHandled){
        this.disconnectHandled = disconnectHandled;
    }
    
    public static void addPermission(UUID uuid, String... permission){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                File file = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
                CompoundTag playerTag;
                if(!file.exists()){
                    playerTag = new CompoundTag();
                } else {
                    NBTInputStream is = new NBTInputStream(file);
                    playerTag = is.read();
                    is.close();
                }
                List<StringTag> permissions = new ArrayList<>(playerTag.getList("Permissions", NBTType.STRING));
                for(String perm : permission){
                    permissions.add(new StringTag(perm));
                }
                playerTag.put("Permissions", new ListTag<>(NBTType.STRING, permissions));
                NBTOutputStream os = new NBTOutputStream(file, playerTag);
                os.write("ConsulatPlayer");
                os.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    
    public static void removePermission(UUID uuid, String... permission){
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                File file = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
                CompoundTag playerTag;
                if(!file.exists()){
                    playerTag = new CompoundTag();
                } else {
                    NBTInputStream is = new NBTInputStream(file);
                    playerTag = is.read();
                    is.close();
                }
                List<StringTag> permissions = new ArrayList<>(playerTag.getList("Permissions", NBTType.STRING));
                for(String perm : permission){
                    permissions.remove(new StringTag(perm));
                }
                playerTag.put("Permissions", new ListTag<>(NBTType.STRING, permissions));
                NBTOutputStream os = new NBTOutputStream(file, playerTag);
                os.write("ConsulatPlayer");
                os.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    
    public void load(){
        try {
            File playerFile = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
            if(!playerFile.exists()){
                return;
            }
            NBTInputStream is = new NBTInputStream(playerFile);
            CompoundTag playerTag = is.read();
            is.close();
            loadNBT(playerTag);
            if(permissions.isEmpty()){
                permissions.addAll(getRankPermissions());
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    void save(){
        try {
            File file = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
            if(!file.exists()){
                if(!file.createNewFile()){
                    throw new IOException("Couldn't create file.");
                }
            }
            CompoundTag playerTag = saveNBT();
            NBTOutputStream os = new NBTOutputStream(file, playerTag);
            os.write("ConsulatPlayer");
            os.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void loadNBT(@NotNull CompoundTag player){
        List<StringTag> list = player.getList("Permissions", NBTType.STRING);
        for(StringTag t : list){
            this.permissions.add(t.getValue());
        }
    }
    
    public CompoundTag saveNBT(){
        CompoundTag player = new CompoundTag();
        ListTag<StringTag> perms = new ListTag<>(NBTType.STRING);
        for(String s : permissions){
            perms.addTag(new StringTag(s));
        }
        player.put("Permissions", perms);
        return player;
    }
    
    public void sendActionBar(String message){
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    
    public boolean isInventoryBlocked(){
        return inventoryBlocked;
    }
    
    public void setInventoryBlocked(boolean inventoryBlocked){
        this.inventoryBlocked = inventoryBlocked;
        if(!inventoryBlocked){
            player.setCanPickupItems(true);
        }
    }
    
    public RFuture<String> getServer(){
        RBucket<String> server = RedisManager.getInstance().getRedis().getBucket(CPlayerManager.getRedisKey(uuid));
        return server.getAsync();
    }
    
    private void setServer(ConsulatServer consulatServer){
        RBucket<String> server = RedisManager.getInstance().getRedis().getBucket(CPlayerManager.getRedisKey(uuid));
        if(consulatServer == null){
            server.deleteAsync();
        } else {
            server.setAsync(consulatServer.name());
        }
    }
    
    public void setServer(){
        setServer(ConsulatAPI.getConsulatAPI().getConsulatServer());
    }
    
    public void disconnected(){
       setServer(null);
    }
}
