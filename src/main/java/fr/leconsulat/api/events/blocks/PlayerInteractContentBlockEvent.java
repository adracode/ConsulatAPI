package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractContentBlockEvent extends PlayerInteractBlockEvent {
    
    private final Type type;
    
    public PlayerInteractContentBlockEvent(Block block, Player player, Type type){
        super(block, player);
        this.type = type;
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
