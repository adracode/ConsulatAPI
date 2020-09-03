package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerOpenEntityInventoryEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerOpenEntityInventoryEvent(Entity entity, Player player){
        super(entity, player);
    }
}
