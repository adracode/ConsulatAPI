package fr.leconsulat.api.events;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ConsulatPlayerLoadedEvent extends Event {
    
    private final ConsulatPlayer player;
    
    public ConsulatPlayerLoadedEvent(ConsulatPlayer player){
        this.player = player;
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
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
