package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerOpenCommandBlockEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerOpenCommandBlockEvent(Entity entity, Player player){
        super(entity, player);
    }
}
