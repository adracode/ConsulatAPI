package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractRedstoneOreEvent extends PlayerInteractBlockEvent{
    public PlayerInteractRedstoneOreEvent(Block block, Player player){
        super(block, player);
    }
    
}
