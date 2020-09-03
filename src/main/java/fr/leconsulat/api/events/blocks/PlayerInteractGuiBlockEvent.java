package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractGuiBlockEvent extends PlayerInteractBlockEvent {
    
    private final EquipmentSlot hand;
    private final Type type;
    
    public PlayerInteractGuiBlockEvent(Block block, Player player, EquipmentSlot hand, Type type){
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
