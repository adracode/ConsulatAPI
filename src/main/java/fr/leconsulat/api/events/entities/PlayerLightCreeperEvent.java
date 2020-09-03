package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerLightCreeperEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerLightCreeperEvent(Entity entity, Player player){
        super(entity, player);
    }
}
