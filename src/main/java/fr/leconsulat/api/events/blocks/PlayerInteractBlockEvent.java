package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public abstract class PlayerInteractBlockEvent extends BlockEvent implements Cancellable {
    
    private final Player player;
    private boolean cancelled;
    
    public PlayerInteractBlockEvent(Block block, Player player){
        super(block);
        this.player = player;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    @Override
    public boolean isCancelled(){
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
    
    private static HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList(){
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers(){
        return handlers;
    }
    
    
}
