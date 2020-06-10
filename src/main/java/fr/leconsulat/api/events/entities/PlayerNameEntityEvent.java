package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerNameEntityEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerNameEntityEvent(Entity entity, Player player){
        super(entity, player);
    }
}
