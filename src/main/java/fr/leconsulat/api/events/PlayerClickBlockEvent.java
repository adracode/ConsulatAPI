package fr.leconsulat.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerClickBlockEvent extends BlockEvent implements Cancellable {
    
    private final Player player;
    private final EquipmentSlot hand;
    private boolean cancelled;
    
    public PlayerClickBlockEvent(Block block, Player player, EquipmentSlot hand){
        super(block);
        this.player = player;
        this.hand = hand;
    }
    
    public EquipmentSlot getHand(){
        return hand;
    }
    
    public ItemStack getItemInHand(){
        return player.getInventory().getItem(hand);
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
