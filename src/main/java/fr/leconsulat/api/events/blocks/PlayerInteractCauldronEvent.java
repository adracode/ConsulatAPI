package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractCauldronEvent extends PlayerInteractBlockEvent {
    public PlayerInteractCauldronEvent(Block block, Player player){
        super(block, player);
    }
}
