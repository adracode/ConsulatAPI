package fr.leconsulat.api.events.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerTurnItemFrameEvent extends PlayerInteractWithEntityEvent {
    
    public PlayerTurnItemFrameEvent(Entity entity, Player player){
        super(entity, player);
    }
}
