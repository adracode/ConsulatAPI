package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractSignEvent extends PlayerInteractBlockEvent {
    public PlayerInteractSignEvent(Block block, Player player){
        super(block, player);
    }
    
}
