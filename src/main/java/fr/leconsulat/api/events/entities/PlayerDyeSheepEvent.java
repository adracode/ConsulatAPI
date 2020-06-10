package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerDyeSheepEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerDyeSheepEvent(Entity entity, Player player){
        super(entity, player);
    }
}
