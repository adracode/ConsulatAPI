package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractPumpkinEvent extends PlayerInteractBlockEvent {
    public PlayerInteractPumpkinEvent(Block block, Player player){
        super(block, player);
    }
    
}
