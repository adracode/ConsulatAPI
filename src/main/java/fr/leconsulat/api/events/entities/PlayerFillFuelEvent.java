package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerFillFuelEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerFillFuelEvent(Entity entity, Player player){
        super(entity, player);
    }
}
