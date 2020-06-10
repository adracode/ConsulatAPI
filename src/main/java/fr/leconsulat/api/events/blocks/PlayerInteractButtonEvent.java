package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractButtonEvent extends PlayerInteractBlockEvent {
    
    public PlayerInteractButtonEvent(Block block, Player player){
        super(block, player);
    }
    
}
