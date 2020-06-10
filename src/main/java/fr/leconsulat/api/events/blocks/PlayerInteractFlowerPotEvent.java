package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractFlowerPotEvent extends PlayerInteractBlockEvent {
    public PlayerInteractFlowerPotEvent(Block block, Player player){
        super(block, player);
    }
    
}
