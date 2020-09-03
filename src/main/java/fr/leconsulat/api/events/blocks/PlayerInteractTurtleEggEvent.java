package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractTurtleEggEvent extends PlayerInteractBlockEvent {
    public PlayerInteractTurtleEggEvent(Block block, Player player){
        super(block, player);
    }
    
}
