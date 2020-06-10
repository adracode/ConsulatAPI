package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractBellEvent extends PlayerInteractBlockEvent {
    
    public PlayerInteractBellEvent(Block block, Player player){
        super(block, player);
    }
    
}
