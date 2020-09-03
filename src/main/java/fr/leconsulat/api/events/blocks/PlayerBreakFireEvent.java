package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerBreakFireEvent extends PlayerInteractBlockEvent {
    
    public PlayerBreakFireEvent(Block block, Player player){
        super(block, player);
    }
}
