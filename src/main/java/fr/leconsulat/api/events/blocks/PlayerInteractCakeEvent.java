package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractCakeEvent extends PlayerInteractBlockEvent {
    public PlayerInteractCakeEvent(Block block, Player player){
        super(block, player);
    }
    
}
