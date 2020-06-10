package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractTntEvent extends PlayerInteractBlockEvent {
    public PlayerInteractTntEvent(Block block, Player player){
        super(block, player);
    }
    
}
