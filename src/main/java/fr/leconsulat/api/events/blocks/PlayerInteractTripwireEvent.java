package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractTripwireEvent extends PlayerInteractBlockEvent {
    public PlayerInteractTripwireEvent(Block block, Player player){
        super(block, player);
    }
    
}
