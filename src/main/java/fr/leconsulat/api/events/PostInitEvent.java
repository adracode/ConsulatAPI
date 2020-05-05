package fr.leconsulat.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PostInitEvent extends Event {
    
    public PostInitEvent(){
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
