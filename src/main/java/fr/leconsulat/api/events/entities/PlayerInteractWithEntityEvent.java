package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class PlayerInteractWithEntityEvent extends EntityEvent implements Cancellable {
    
    private final Player player;
    private boolean cancelled;
    
    public PlayerInteractWithEntityEvent(Entity entity, Player player){
        super(entity);
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
