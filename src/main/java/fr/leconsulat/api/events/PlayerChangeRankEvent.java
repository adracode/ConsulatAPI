package fr.leconsulat.api.events;

import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeRankEvent extends Event {
    
    private final ConsulatPlayer player;
    private final Rank newRank;
    
    public PlayerChangeRankEvent(ConsulatPlayer player, Rank newRank){
        this.player = player;
        this.newRank = newRank;
    }
    
    public ConsulatPlayer getPlayer(){
        return player;
    }
    
    public Rank getNewRank(){
        return newRank;
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
