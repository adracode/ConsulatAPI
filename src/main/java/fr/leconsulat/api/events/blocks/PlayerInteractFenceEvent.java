package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractFenceEvent extends PlayerInteractBlockEvent {
    public PlayerInteractFenceEvent(Block block, Player player){
        super(block, player);
    }
    
}
