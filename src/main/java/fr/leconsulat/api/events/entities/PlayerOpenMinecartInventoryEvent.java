package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerOpenMinecartInventoryEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerOpenMinecartInventoryEvent(Entity entity, Player player){
        super(entity, player);
    }
}
