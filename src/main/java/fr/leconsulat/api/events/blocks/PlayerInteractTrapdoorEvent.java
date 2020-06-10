package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractTrapdoorEvent extends PlayerInteractBlockEvent {
    public PlayerInteractTrapdoorEvent(Block block, Player player){
        super(block, player);
    }
    
}
