package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractNoteEvent extends PlayerInteractBlockEvent {
    public PlayerInteractNoteEvent(Block block, Player player){
        super(block, player);
    }
    
}
