package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerFillBowlEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerFillBowlEvent(Entity entity, Player player){
        super(entity, player);
    }
}
