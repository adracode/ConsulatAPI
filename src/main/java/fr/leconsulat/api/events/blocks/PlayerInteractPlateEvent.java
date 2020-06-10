package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractPlateEvent extends PlayerInteractBlockEvent {
    public PlayerInteractPlateEvent(Block block, Player player){
        super(block, player);
    }
    
}
