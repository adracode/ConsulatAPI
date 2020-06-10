package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractGuiBlockEvent extends PlayerInteractBlockEvent {
    
    private final Type type;
    
    public PlayerInteractGuiBlockEvent(Block block, Player player, Type type){
        super(block, player);
        this.type = type;
    }
    
    public Type getType(){
        return type;
    }
    
public enum Type {
        ANVIL,
        BEACON,
        CARTOGRAPHY_TABLE,
        CRAFTING_TABLE,
        ENCHANTING_TABLE,
        ENDER_CHEST,
        FLETCHING_TABLE,
        GRINDSTONE,
        LOOM,
        SMITHING_TABLE,
        STONECUTTER
    }
    
}
