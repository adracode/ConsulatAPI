package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerPickupFishEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerPickupFishEvent(Entity entity, Player player){
        super(entity, player);
    }
    
}
