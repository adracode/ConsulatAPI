package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractBerryEvent extends PlayerInteractBlockEvent {
    public PlayerInteractBerryEvent(Block block, Player player){
        super(block, player);
    }
}
