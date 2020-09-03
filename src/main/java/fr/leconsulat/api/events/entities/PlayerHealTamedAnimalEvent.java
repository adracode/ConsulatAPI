package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerHealTamedAnimalEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerHealTamedAnimalEvent(Entity entity, Player player){
        super(entity, player);
    }
}
