package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractForbiddenEvent extends PlayerInteractBlockEvent {
    public PlayerInteractForbiddenEvent(Block block, Player player){
        super(block, player);
    }
    
}
