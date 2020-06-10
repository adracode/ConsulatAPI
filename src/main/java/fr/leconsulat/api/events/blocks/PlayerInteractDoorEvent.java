package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractDoorEvent extends PlayerInteractBlockEvent {
    
    public PlayerInteractDoorEvent(Block block, Player player){
        super(block, player);
    }
    
}
