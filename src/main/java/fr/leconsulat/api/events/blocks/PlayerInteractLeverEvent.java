package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractLeverEvent extends PlayerInteractBlockEvent {
    public PlayerInteractLeverEvent(Block block, Player player){
        super(block, player);
    }
    
}
