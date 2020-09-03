package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractContainerBlockEvent extends PlayerInteractBlockEvent {
    
    private final EquipmentSlot hand;
    private final Type type;
    
    public PlayerInteractContainerBlockEvent(Block block, Player player, EquipmentSlot hand, Type type){
        super(block, player);
        this.hand = hand;
        this.type = type;
    }
    
    public EquipmentSlot getHand(){
        return hand;
    }
    
    
    public Type getType(){
        return type;
    }
    
    public enum Type {
        BARREL,
        BLAST_FURNACE,
        BREWING_STAND,
        CHEST,
        DISPENSER,
        DROPPER,
        FURNACE,
        HOPPER,
        SHULKER_BOX,
        SMOKER
        
    }
    
}
