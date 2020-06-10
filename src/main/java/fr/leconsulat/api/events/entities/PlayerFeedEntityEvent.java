package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerFeedEntityEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerFeedEntityEvent(Entity entity, Player player){
        super(entity, player);
    }
}
