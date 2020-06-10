package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerChangeCollarColorEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerChangeCollarColorEvent(Entity entity, Player player){
        super(entity, player);
    }
}
