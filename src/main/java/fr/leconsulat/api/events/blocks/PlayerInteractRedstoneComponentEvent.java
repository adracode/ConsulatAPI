package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractRedstoneComponentEvent extends PlayerInteractBlockEvent {
    public PlayerInteractRedstoneComponentEvent(Block block, Player player){
        super(block, player);
    }
    
}
