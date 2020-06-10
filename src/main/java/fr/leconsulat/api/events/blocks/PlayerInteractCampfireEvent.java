package fr.leconsulat.api.events.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerInteractCampfireEvent extends PlayerInteractBlockEvent {
    public PlayerInteractCampfireEvent(Block block, Player player){
        super(block, player);
    }
}
