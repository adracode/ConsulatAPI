package fr.leconsulat.api.events;

import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ConsulatPlayerLeaveEvent extends Event {
    
    private final ConsulatPlayer player;
    
    public ConsulatPlayerLeaveEvent(ConsulatPlayer player){
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
