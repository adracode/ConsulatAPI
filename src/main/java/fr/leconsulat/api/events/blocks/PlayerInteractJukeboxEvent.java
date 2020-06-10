package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractJukeboxEvent extends PlayerInteractBlockEvent {
    public PlayerInteractJukeboxEvent(Block block, Player player){
        super(block, player);
    }
    
}
