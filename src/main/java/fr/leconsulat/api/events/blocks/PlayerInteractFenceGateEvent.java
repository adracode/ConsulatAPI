package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractFenceGateEvent extends PlayerInteractBlockEvent {
    public PlayerInteractFenceGateEvent(Block block, Player player){
        super(block, player);
    }
    
}
