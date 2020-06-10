package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractFarmlandEvent extends PlayerInteractBlockEvent {
    public PlayerInteractFarmlandEvent(Block block, Player player){
        super(block, player);
    }
    
}
