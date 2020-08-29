package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerOpenVillagerEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerOpenVillagerEvent(Entity entity, Player player){
        super(entity, player);
    }
    
}
