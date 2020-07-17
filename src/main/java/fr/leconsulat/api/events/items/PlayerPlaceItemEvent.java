package fr.leconsulat.api.events.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerPlaceItemEvent extends PlayerEvent implements Cancellable {
    
    private final @NotNull Location clickedLocation;
    private final @NotNull ItemStack item;
    private boolean cancelled = false;
    
    public PlayerPlaceItemEvent(Player player, @NotNull Location clickedLocation, @NotNull ItemStack item){
        super(player);
        this.clickedLocation = clickedLocation;
        this.item = item;
    }
    
    public Location getClickedLocation(){
        return clickedLocation;
    }
    
    public @NotNull ItemStack getItem(){
        return item;
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
