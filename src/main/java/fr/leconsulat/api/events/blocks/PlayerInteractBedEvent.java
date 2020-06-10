package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractBedEvent extends PlayerInteractBlockEvent {
    
    public PlayerInteractBedEvent(Block block, Player player){
        super(block, player);
    }
    
}
