package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerSaddlePigEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerSaddlePigEvent(Entity entity, Player player){
        super(entity, player);
    }
}
