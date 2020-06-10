package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractComposterEvent extends PlayerInteractBlockEvent {
    public PlayerInteractComposterEvent(Block block, Player player){
        super(block, player);
    }
}
