package fr.leconsulat.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatSyncEvent extends Event {
    
    private final boolean isSync;
    
    public ChatSyncEvent(boolean isSync){
        this.isSync = isSync;
    }
    
    public boolean isSync(){
        return isSync;
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
