package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractLecternEvent extends PlayerInteractBlockEvent {
    public PlayerInteractLecternEvent(Block block, Player player){
        super(block, player);
    }
    
}


