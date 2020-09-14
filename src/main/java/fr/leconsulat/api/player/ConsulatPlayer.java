package fr.leconsulat.api.player;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.ConsulatServer;
import fr.leconsulat.api.channel.Channel;
import fr.leconsulat.api.channel.ChannelManager;
import fr.leconsulat.api.channel.Speakable;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.commands.commands.PersoCommand;
import fr.leconsulat.api.database.Saveable;
import fr.leconsulat.api.events.PlayerChangeRankEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.moderation.BanReason;
import fr.leconsulat.api.moderation.MuteReason;
import fr.leconsulat.api.moderation.MutedPlayer;
import fr.leconsulat.api.nbt.*;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.utils.FileUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"unused"})
public class ConsulatPlayer implements Saveable {
    
    private static final String SPEAK_PERM = ConsulatAPI.getConsulatAPI().getPermission("bypass-chat");
    public static final String ERROR = ConsulatAPI.getConsulatAPI().getPermission("error");
    
    private final @NotNull UUID uuid;
    private final @NotNull String name;
    private final @NotNull Player player;
    private int id;
    private @NotNull Set<String> permissions = new HashSet<>();
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
    private boolean isMuted;
    private long muteExpireMillis;
    private String muteReason;
    private HashMap<BanReason, Integer> banHistory = new HashMap<>();
    private HashMap<MuteReason, Integer> muteHistory = new HashMap<>();
    private CustomRankState customRankState;
    
    public ConsulatPlayer(@NotNull UUID uuid, @NotNull String name){
        this.uuid = Objects.requireNonNull(uuid);
        this.name = Objects.requireNonNull(name);
        this.player = Objects.requireNonNull(Bukkit.getPlayer(uuid), "player");
        setInventoryBlocked(true);
    }
    
    public int getId(){
        return id;
    }
    
    public @NotNull UUID getUUID(){
        return uuid;
    }
    
    public @NotNull String getName(){
        return name;
    }
    
    public @NotNull Player getPlayer(){
        return player;
    }
    
    public @NotNull Rank getRank(){
        return rank;
    }
    
    public void setRank(@NotNull Rank rank){
        Objects.requireNonNull(rank, "rank");
        
        PlayerChangeRankEvent event = new PlayerChangeRankEvent(this, rank);
        Bukkit.getPluginManager().callEvent(event);
        CPlayerManager.getInstance().setRank(getUUID(), rank);
        this.rank = rank;
        for(Command command : CommandManager.getInstance().getCommands().values()){
            if(command instanceof ConsulatCommand){
                ConsulatCommand consulatCommand = (ConsulatCommand)command;
                if(consulatCommand.getRank() != null){
                    if(hasPower(consulatCommand.getRank())){
                        addPermission(consulatCommand.getPermission());
                    } else {
                        removePermission(consulatCommand.getPermission());
                    }
                }
            }
        }
        permissions.addAll(CPlayerManager.getInstance().getDefaultPermissions(this));
        CommandManager.getInstance().sendCommands(this);
    }
    
    public boolean isInitialized(){
        return initialized;
    }
    
    public @Nullable String getCustomRank(){
        if(!hasCustomRank()){
            throw new NullPointerException("CustomRank is null, use ConsulatPlayer#hasCustomRank() to check");
        }
        return customRank.getCustomRank();
    }
    
    public @Nullable String getCustomPrefix(){
        if(!hasCustomRank()){
            throw new NullPointerException("CustomRank is null, use ConsulatPlayer#hasCustomRank() to check");
        }
        return customRank.getCustomPrefix();
    }
    
    public CustomRankState getPersoState(){
        return customRankState;
    }
    
    public void setPersoState(CustomRankState customRankState){
        this.customRankState = customRankState;
    }
    
    public String chat(String message){
        boolean cancel = false;
        if(getCurrentChannel() == null){
            if(!ConsulatAPI.getConsulatAPI().isChat() && !hasPermission(SPEAK_PERM)){
                sendMessage("§cChat coupé.");
                cancel = true;
            }
        }
        if(getPersoState() == CustomRankState.PREFIX){
            if(message.equalsIgnoreCase("cancel")){
                resetCustomRank();
                setPersoState(CustomRankState.START);
                sendMessage("§aChangement de grade annulé.");
                return null;
            }
            if(message.length() > 10){
                sendMessage("§cTon grade doit faire 10 caractères maximum ! Tape §ocancel §r§csi tu veux annuler.");
                return null;
            }
            PersoCommand persoCommand = (PersoCommand)CommandManager.getInstance().getCommand("perso");
            if(persoCommand.isCustomRankForbidden(message)){
                sendMessage("§cTu ne peux pas appeler ton grade comme cela ! Tape §ocancel §r§csi tu veux annuler.");
                return null;
            }
            if(!message.matches("^[a-zA-Z]+$")){
                sendMessage("§cTu dois utiliser uniquement des lettres dans ton grade.");
                return null;
            }
            setPrefix(message);
            setPersoState(CustomRankState.NAME_COLOR);
            sendMessage("§6Voici ton grade: " + getCustomPrefix());
            sendMessage("§7Maintenant, choisis la couleur de ton pseudo:");
            sendMessage(persoCommand.getCustomRankColors());
            return null;
        }
        if(isMuted() && System.currentTimeMillis() < getMuteExpireMillis()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getMuteExpireMillis());
            String resultDate = ConsulatAPI.getConsulatAPI().DATE_FORMAT.format(calendar.getTime());
            String reason = getMuteReason();
            sendMessage("§cTu es actuellement mute.\n§4Raison: §c" + reason + "\n§4Jusqu'au: §c" + resultDate);
            return null;
        }
        if(cancel){
            return null;
        }
        Channel channel = getCurrentChannel();
        if(channel == null){
            if(hasPower(Rank.MODO)){
                return ChatColor.translateAlternateColorCodes('&', message);
            }
        } else {
            if(channel instanceof Speakable){
                channel.sendMessage(((Speakable)channel).speak(this, message));
            }
            return null;
        }
        return message;
    }
    
    public boolean isError(){
        return hasPermission(ERROR);
    }
    
    public void setError(boolean error){
        if(error){
            addPermission(ERROR);
        } else {
            removePermission(ERROR);
        }
    }
    
    public @NotNull String getRegistered(){
        if(!isInitialized()){
            throw new NullPointerException("Registered is null, use ConsulatPlayer#isInitialized() to check");
        }
        return registered;
    }
    
    public long getMuteExpireMillis(){
        return muteExpireMillis;
    }
    
    public void setMuteExpireMillis(long muteExpireMillis){
        this.muteExpireMillis = muteExpireMillis;
    }
    
    public String getMuteReason(){
        return muteReason;
    }
    
    public void setMuteReason(String reason){
        this.muteReason = reason;
    }
    
    public boolean isMuted(){
        return isMuted;
    }
    
    public void setMuted(boolean muted){
        isMuted = muted;
    }
    
    public @Nullable MutedPlayer getMute(){
        if(System.currentTimeMillis() < muteExpireMillis){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(muteExpireMillis);
            String resultDate = ConsulatAPI.getConsulatAPI().DATE_FORMAT.format(calendar.getTime());
            String reason = muteReason;
            return new MutedPlayer(reason, resultDate);
        }
        return null;
    }
    
    public boolean isVanished(){
        return vanished;
    }
    
    public void setVanished(boolean vanished){
        this.vanished = vanished;
    }
    
    public @Nullable IGui getCurrentlyOpen(){
        return currentlyOpen;
    }
    
    public void setCurrentlyOpen(@Nullable IGui gui){
        this.currentlyOpen = gui;
    }
    
    public @Nullable Channel getCurrentChannel(){
        return currentChannel;
    }
    
    public void setCurrentChannel(@Nullable Channel currentChannel){
        this.currentChannel = currentChannel;
    }
    
    public @NotNull String getDisplayName(){
        return getDisplayRank() + " " + getName();
    }
    
    public @NotNull String getDisplayRank(){
        if(!isInitialized()){
            return "§f";
        }
        if(!hasCustomRank()){
            return rank.getRankColor() + "[" + rank.getRankName() + "]";
        }
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
    
    public boolean isDisconnectHandled(){
        return disconnectHandled;
    }
    
    public void setDisconnectHandled(boolean disconnectHandled){
        this.disconnectHandled = disconnectHandled;
    }
    
    public boolean isInventoryBlocked(){
        return inventoryBlocked;
    }
    
    public void setInventoryBlocked(boolean inventoryBlocked){
        this.inventoryBlocked = inventoryBlocked;
        player.setCanPickupItems(!inventoryBlocked);
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
    
    public void setHasCustomRank(boolean hasCustomRank){
        CPlayerManager.getInstance().setHasCustomRank(getUUID(), hasCustomRank);
        if(hasCustomRank){
            customRank = new CustomRank();
            addCommandPermission(CommandManager.getInstance().getCommand("perso").getPermission());
        } else {
            customRank = null;
            removeCommandPermission(CommandManager.getInstance().getCommand("perso").getPermission());
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
    
    public void initialize(int id, Rank rank, boolean hasCustomRank, String customRank, String registered){
        this.id = id;
        this.rank = rank;
        this.customRank = hasCustomRank ? customRank == null ? new CustomRank() : new CustomRank(customRank) : null;
        this.registered = registered;
        this.initialized = true;
    }
    
    public HashMap<BanReason, Integer> getBanHistory(){
        return banHistory;
    }
    
    public HashMap<MuteReason, Integer> getMuteHistory(){
        return muteHistory;
    }
    
    
    public void onQuit(){
        if(isInitialized()){
            save();
        }
        Channel staffChannel = ChannelManager.getInstance().getChannel("staff");
        if(staffChannel.isMember(this)){
            staffChannel.removePlayer(this);
        }
    }
    
    public boolean hasCustomRank(){
        return customRank != null;
    }
    
    public void resetCustomRank(){
        if(!hasCustomRank()){
            return;
        }
        CPlayerManager.getInstance().setCustomRank(getUUID(), null);
        this.customRank.reset();
    }
    
    public void applyCustomRank(){
        if(!hasCustomRank()){
            return;
        }
        CPlayerManager.getInstance().setCustomRank(getUUID(), customRank.getCustomRank().replace('§', '&'));
    }
    
    public boolean hasPower(Rank neededRank){
        return this.rank != null && neededRank != null && this.rank.getRankPower() >= neededRank.getRankPower();
    }
    
    public void sendMessage(String message){
        getPlayer().sendMessage(message);
    }
    
    public void sendMessage(BaseComponent... message){
        getPlayer().spigot().sendMessage(message);
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
    
    public void addCommandPermission(String permission){
        addPermission(permission);
        CommandManager.getInstance().sendCommands(this);
    }
    
    public void removeCommandPermission(String permission){
        removePermission(permission);
        CommandManager.getInstance().sendCommands(this);
    }
    
    public void addPermission(String permission){
        this.permissions.add(permission);
    }
    
    public void removePermission(String permission){
        this.permissions.remove(permission);
    }
    
    public void resetPermissions(){
        permissions.clear();
        giveDefaultPermissions();
    }
    
    public int decrementPosition(){
        return --positionInQueue;
    }
    
    public void load(){
        try {
            File playerFile = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
            if(!playerFile.exists()){
                giveDefaultPermissions();
                return;
            }
            NBTInputStream is = new NBTInputStream(playerFile);
            CompoundTag playerTag = is.read();
            is.close();
            loadNBT(playerTag);
            if(permissions.isEmpty()){
                giveDefaultPermissions();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void giveDefaultPermissions(){
        for(Command command : CommandManager.getInstance().getCommands().values()){
            if(command instanceof ConsulatCommand){
                ConsulatCommand consulatCommand = (ConsulatCommand)command;
                if(hasPower(consulatCommand.getRank())){
                    addPermission(consulatCommand.getPermission());
                }
            }
        }
        permissions.addAll(CPlayerManager.getInstance().getDefaultPermissions(this));
    }
    
    public void loadNBT(@NotNull CompoundTag player){
        List<StringTag> list = player.getList("Permissions", NBTType.STRING);
        for(StringTag t : list){
            this.permissions.add(t.getValue());
        }
        if(player.has("LastKnownRank")){
            Rank lastRank = Rank.valueOf(player.getString("LastKnownRank"));
            if(lastRank != rank){
                if(lastRank.getRankPower() >= rank.getRankPower()){
                    permissions.clear();
                }
                giveDefaultPermissions();
            }
        }
        if(player.has("DefaultPermissions")){
            resetPermissions();
        }
    }
    
    public CompoundTag saveNBT(){
        CompoundTag player = new CompoundTag();
        ListTag<StringTag> perms = new ListTag<>(NBTType.STRING);
        for(String s : permissions){
            perms.addTag(new StringTag(s));
        }
        player.put("Permissions", perms);
        player.putString("LastKnownRank", rank.name());
        return player;
    }
    
    public void sendActionBar(String message){
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    
    public void setServer(){
        setServer(ConsulatAPI.getConsulatAPI().getConsulatServer());
    }
    
    public void disconnected(){
        setServer(null);
    }
    
    public synchronized void save(){
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
    
    @Override
    public int hashCode(){
        return uuid.hashCode();
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
    
    public static boolean addPermission(UUID uuid, String... permission){
        try {
            File file = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
            CompoundTag playerTag;
            if(!file.exists()){
                return false;
            }
            NBTInputStream is = new NBTInputStream(file);
            playerTag = is.read();
            is.close();
            boolean added = false;
            List<StringTag> permissions = new ArrayList<>(playerTag.getList("Permissions", NBTType.STRING));
            for(String perm : permission){
                added |= permissions.add(new StringTag(perm));
            }
            playerTag.put("Permissions", new ListTag<>(NBTType.STRING, permissions));
            NBTOutputStream os = new NBTOutputStream(file, playerTag);
            os.write("ConsulatPlayer");
            os.close();
            return added;
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasPermission(UUID uuid, String permission){
        try {
            File file = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
            CompoundTag playerTag;
            if(!file.exists()){
                return false;
            }
            NBTInputStream is = new NBTInputStream(file);
            playerTag = is.read();
            is.close();
            Set<StringTag> permissions = new HashSet<>(playerTag.getList("Permissions", NBTType.STRING));
            return permissions.contains(new StringTag(permission));
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removePermission(UUID uuid, String... permission){
        try {
            File file = FileUtils.loadFile(ConsulatAPI.getConsulatAPI().getDataFolder(), "players/" + uuid + ".dat");
            CompoundTag playerTag;
            if(!file.exists()){
                return false;
            }
            NBTInputStream is = new NBTInputStream(file);
            playerTag = is.read();
            is.close();
            boolean removed = false;
            List<StringTag> permissions = new ArrayList<>(playerTag.getList("Permissions", NBTType.STRING));
            for(String perm : permission){
                removed |= permissions.remove(new StringTag(perm));
            }
            playerTag.put("Permissions", new ListTag<>(NBTType.STRING, permissions));
            NBTOutputStream os = new NBTOutputStream(file, playerTag);
            os.write("ConsulatPlayer");
            os.close();
            return removed;
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public static void resetPermissions(UUID uuid){
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
            playerTag.putByte("DefaultPermissions", (byte)1);
            NBTOutputStream os = new NBTOutputStream(file, playerTag);
            os.write("ConsulatPlayer");
            os.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
