package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractDragonEggEvent extends PlayerInteractBlockEvent {
    public PlayerInteractDragonEggEvent(Block block, Player player){
        super(block, player);
    }
    
}
