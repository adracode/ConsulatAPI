package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractDragonEggEvent extends PlayerInteractBlockEvent {
    private final EquipmentSlot hand;
    public PlayerInteractDragonEggEvent(Block block, Player player, EquipmentSlot hand){
        super(block, player);
        this.hand = hand;
    }
    
    public EquipmentSlot getHand(){
        return hand;
    }
    
}
