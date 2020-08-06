package fr.leconsulat.api.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import fr.leconsulat.api.redis.RedisManager;
import fr.leconsulat.api.utils.StringUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class Server {
    
    private final @NotNull String name;
    private int players = 0;
    private int slot = 0;
    private final @Nullable PlayerQueue queue;
    
    public Server(@NotNull String name, boolean queue){
        this.name = Objects.requireNonNull(name);
        this.queue = queue ? new PlayerQueue() : null;
        RedisManager.getInstance().register("Player" + StringUtils.capitalize(name), int.class,
                (channel, players) -> setPlayers(players));
        RedisManager.getInstance().register("Slot" + StringUtils.capitalize(name), int.class,
                (channel, slot) -> setSlot(slot));
    }
    
    public abstract Plugin getPlugin();
    
    public @NotNull String getName(){
        return name;
    }
    
    public int getPlayers(){
        return players;
    }
    
    public synchronized void setPlayers(int players){
        this.players = players;
        onPlayerChange();
        if(queue != null && slot > 0 && players < slot){
            ConsulatPlayer player = queue.pickPlayer();
            while(player != null && !player.getPlayer().isOnline()){
                player = queue.pickPlayer();
            }
            if(player == null){
                return;
            }
            connectPlayer(player);
            int queueSize = queue.size();
            for(ConsulatPlayer queuePlayer : queue){
                queuePlayer.sendMessage("§aTu es dans la queue: " +
                        queuePlayer.decrementPosition() + " / " + queueSize);
            }
        }
    }
    
    public void onPlayerChange(){
    }
    
    public void onSlotChange(){
    }
    
    public void onPlayerConnect(ConsulatPlayer player){
    }
    
    public int getSlot(){
        return slot;
    }
    
    public void setSlot(int slot){
        this.slot = slot;
        onSlotChange();
    }
    
    public ConnectResult queuePlayer(ConsulatPlayer player){
        if(queue == null){
            if(this.players >= slot){
                return ConnectResult.SERVER_FULL;
            }
            connectPlayer(player);
            return ConnectResult.CONNECT;
        }
        if(player.isInQueue()){
            return ConnectResult.ALREADY_IN_QUEUE;
        }
        if((this.players < slot && queue.isEmpty()) || player.hasPower(Rank.FINANCEUR) || player.getRank() == Rank.TOURISTE){
            connectPlayer(player);
            return ConnectResult.CONNECT;
        }
        player.setPositionInQueue(queue.isEmpty() ? 1 : queue.last().getPositionInQueue() + 1);
        queue.addPlayer(player);
        return ConnectResult.IN_QUEUE;
    }
    
    public void connectPlayer(ConsulatPlayer player){
        onPlayerConnect(player);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(name);
        player.getPlayer().sendPluginMessage(getPlugin(), "BungeeCord", out.toByteArray());
    }
    
    public int getPlayersInQueue(){
        return queue == null ? 0 : queue.size();
    }
    
    public enum ConnectResult {
        IN_QUEUE,
        ALREADY_IN_QUEUE,
        SERVER_FULL,
        CONNECT
    }
    
}
